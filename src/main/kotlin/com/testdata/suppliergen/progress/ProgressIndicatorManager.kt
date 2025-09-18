package com.testdata.suppliergen.progress

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

/**
 * Manages progress indicator integration for supplier generation.
 * Ensures compliance with JetBrains IDE policies by coordinating with write operations.
 */
class ProgressIndicatorManager {

    /**
     * Execute generation with progress tracking in background.
     * The actual write operations happen within WriteCommandAction in the task.
     */
    fun executeWithProgress(
        project: Project,
        title: String = "Generating Supplier Classes",
        totalFields: Int,
        task: (ProgressCallback) -> Unit
    ) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, title, true) {
            override fun run(indicator: ProgressIndicator) {
                val tracker = ProgressTracker()
                val callback = ProgressCallbackImpl(indicator, tracker)

                // Initialize progress
                tracker.initialize(totalFields)

                try {
                    // Execute the generation task with progress callback
                    task(callback)
                } catch (e: Exception) {
                    indicator.text = "Generation failed: ${e.message}"
                    throw e
                } finally {
                    if (!indicator.isCanceled) {
                        indicator.text = "Generation completed"
                        indicator.fraction = 1.0
                    }
                }
            }
        })
    }

    /**
     * Execute generation with modal progress dialog.
     * Use for smaller operations or when user interaction is needed.
     */
    fun executeWithModalProgress(
        project: Project,
        title: String = "Generating Supplier Classes",
        totalFields: Int,
        task: (ProgressCallback) -> Unit
    ) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously({
            val indicator = ProgressManager.getInstance().progressIndicator
            val tracker = ProgressTracker()
            val callback = ProgressCallbackImpl(indicator, tracker)

            tracker.initialize(totalFields)
            task(callback)
        }, title, true, project)
    }

    /**
     * Create a lightweight progress callback for use within write operations.
     * This is safe to use inside WriteCommandAction.
     */
    fun createWriteOperationCallback(totalFields: Int): ProgressCallback {
        val tracker = ProgressTracker()
        tracker.initialize(totalFields)
        return WriteOperationProgressCallback(tracker)
    }

    private class ProgressCallbackImpl(
        private val indicator: ProgressIndicator,
        private val tracker: ProgressTracker
    ) : ProgressCallback {

        override fun updatePhase(phase: ProgressTracker.GenerationPhase) {
            val progress = tracker.updatePhase(phase)
            indicator.text = phase.displayName
            updateIndicator(progress)
        }

        override fun incrementField(fieldName: String) {
            val progress = tracker.incrementField(fieldName)
            indicator.text2 = "Processing field: $fieldName"
            updateIndicator(progress)
        }

        override fun setDetailText(text: String) {
            indicator.text2 = text
        }

        override fun checkCanceled() {
            indicator.checkCanceled()
        }

        override fun isCanceled(): Boolean = indicator.isCanceled

        private fun updateIndicator(progress: ProgressTracker.ProgressInfo) {
            indicator.fraction = progress.progressPercent / 100.0

            if (progress.estimatedTimeRemaining > 0) {
                val seconds = progress.estimatedTimeRemaining / 1000
                indicator.text2 = "${indicator.text2} (${seconds}s remaining)"
            }
        }
    }

    /**
     * Lightweight progress callback for write operations.
     * Doesn't interact with UI components that might violate write operation constraints.
     */
    private class WriteOperationProgressCallback(
        private val tracker: ProgressTracker
    ) : ProgressCallback {

        override fun updatePhase(phase: ProgressTracker.GenerationPhase) {
            tracker.updatePhase(phase)
            // Log progress for debugging without UI interaction
            println("Generation phase: ${phase.displayName}")
        }

        override fun incrementField(fieldName: String) {
            val progress = tracker.incrementField(fieldName)
            println("Processing field $fieldName (${progress.processedFields}/${progress.totalFields})")
        }

        override fun setDetailText(text: String) {
            println("Detail: $text")
        }

        override fun checkCanceled() {
            // Check for interruption without UI dependency
            if (Thread.currentThread().isInterrupted) {
                throw InterruptedException("Generation was interrupted")
            }
        }

        override fun isCanceled(): Boolean = Thread.currentThread().isInterrupted
    }
}

/**
 * Progress callback interface for field-level progress tracking
 */
interface ProgressCallback {
    fun updatePhase(phase: ProgressTracker.GenerationPhase)
    fun incrementField(fieldName: String)
    fun setDetailText(text: String)
    fun checkCanceled()
    fun isCanceled(): Boolean
}