package com.testdata.suppliergen.progress

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass

/**
 * Example usage of the progress tracking system for supplier generation.
 * Demonstrates how to integrate progress tracking in different scenarios.
 */
class ProgressUsageExample {

    /**
     * Example 1: Background generation with progress bar
     */
    fun generateWithBackgroundProgress(project: Project, targetClass: PsiClass) {
        val progressManager = ProgressIndicatorManager()
        val progressTracker = ProgressTracker()
        val totalFields = progressTracker.calculateTotalFields(targetClass)

        progressManager.executeWithProgress(
            project = project,
            title = "Generating ${targetClass.name} Supplier",
            totalFields = totalFields
        ) { progressCallback ->

            // Phase 1: Analysis
            progressCallback.updatePhase(ProgressTracker.GenerationPhase.FIELD_ANALYSIS)
            analyzeClassStructure(targetClass, progressCallback)

            // Phase 2: Code Generation
            progressCallback.updatePhase(ProgressTracker.GenerationPhase.CODE_GENERATION)
            generateSupplierCode(targetClass, progressCallback)

            // Phase 3: File Creation
            progressCallback.updatePhase(ProgressTracker.GenerationPhase.FILE_CREATION)
            createOutputFiles(progressCallback)
        }
    }

    /**
     * Example 2: Modal dialog progress (for smaller operations)
     */
    fun generateWithModalProgress(project: Project, targetClass: PsiClass) {
        val progressManager = ProgressIndicatorManager()
        val progressTracker = ProgressTracker()
        val totalFields = progressTracker.calculateTotalFields(targetClass)

        progressManager.executeWithModalProgress(
            project = project,
            title = "Quick Generate: ${targetClass.name}",
            totalFields = totalFields
        ) { progressCallback ->
            generateSupplierCode(targetClass, progressCallback)
        }
    }

    /**
     * Example 3: Write operation with lightweight progress tracking
     */
    fun generateWithWriteOperationProgress(totalFields: Int) {
        val progressManager = ProgressIndicatorManager()
        val progressCallback = progressManager.createWriteOperationCallback(totalFields)

        // This can be safely used inside WriteCommandAction
        processFieldsWithProgress(progressCallback)
    }

    private fun analyzeClassStructure(targetClass: PsiClass, progressCallback: ProgressCallback) {
        val fields = targetClass.fields

        progressCallback.setDetailText("Analyzing ${fields.size} fields...")

        fields.forEach { field ->
            progressCallback.checkCanceled() // Check for user cancellation

            progressCallback.incrementField(field.name ?: "unnamed")

            // Simulate analysis work
            analyzeFieldType(field)
        }
    }

    private fun generateSupplierCode(targetClass: PsiClass, progressCallback: ProgressCallback) {
        val fields = targetClass.fields

        fields.forEach { field ->
            progressCallback.checkCanceled()

            val fieldName = field.name ?: "unnamed"
            progressCallback.incrementField(fieldName)
            progressCallback.setDetailText("Generating supplier for $fieldName")

            // Simulate code generation
            generateFieldSupplier(field)
        }
    }

    private fun createOutputFiles(progressCallback: ProgressCallback) {
        progressCallback.setDetailText("Writing supplier file...")
        // Simulate file creation
        Thread.sleep(100)
        progressCallback.setDetailText("Writing test data class...")
        Thread.sleep(50)
    }

    private fun processFieldsWithProgress(progressCallback: ProgressCallback) {
        // Example of processing fields with progress tracking
        // This method is safe to use inside WriteCommandAction

        progressCallback.updatePhase(ProgressTracker.GenerationPhase.CODE_GENERATION)

        val sampleFields = listOf("id", "name", "email", "phone", "address")

        sampleFields.forEach { fieldName ->
            progressCallback.incrementField(fieldName)

            // Simulate processing
            processField(fieldName)
        }
    }

    private fun analyzeFieldType(field: com.intellij.psi.PsiField) {
        // Simulate field analysis
        Thread.sleep(5)
    }

    private fun generateFieldSupplier(field: com.intellij.psi.PsiField) {
        // Simulate supplier generation
        Thread.sleep(10)
    }

    private fun processField(fieldName: String) {
        // Simulate field processing
        Thread.sleep(20)
    }
}

/**
 * Configuration class for progress tracking behavior
 */
data class ProgressConfiguration(
    val showEstimatedTime: Boolean = true,
    val showDetailedFieldInfo: Boolean = true,
    val enableCancellation: Boolean = true,
    val updateIntervalMs: Long = 100,
    val showPhaseTransitions: Boolean = true
) {
    companion object {
        val DEFAULT = ProgressConfiguration()
        val MINIMAL = ProgressConfiguration(
            showEstimatedTime = false,
            showDetailedFieldInfo = false,
            showPhaseTransitions = false
        )
        val DETAILED = ProgressConfiguration(
            showEstimatedTime = true,
            showDetailedFieldInfo = true,
            showPhaseTransitions = true,
            updateIntervalMs = 50
        )
    }
}