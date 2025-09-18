package com.testdata.suppliergen.v2.v3

import com.testdata.suppliergen.v2.*
import com.testdata.suppliergen.generator.SupplierGenerator
import com.testdata.suppliergen.progress.ProgressCallback
import com.testdata.suppliergen.progress.ProgressTracker
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory

// 3. GenerationPerformer - Minor fix to pass correct file to TestDataMethodAdder
class GenerationPerformer {
    private val contextBuilder = GenerationContextBuilder()
    private val fileCoordinator = FileGenerationCoordinator()
    private val fileProcessor = GeneratedFileProcessor()
    private val navigator = FileNavigator()
    private val testDataMethodAdder = TestDataMethodAdder()

    fun perform(
        context: ActionContext,
        targetClass: PsiClass,
        options: DialogOptions,
        targetDir: PsiDirectory,
        progressCallback: ProgressCallback
    ) {
        progressCallback.updatePhase(ProgressTracker.GenerationPhase.FIELD_ANALYSIS)

        val ctx = contextBuilder.buildContext(context.project, targetDir, options)

        progressCallback.updatePhase(ProgressTracker.GenerationPhase.TYPE_RESOLUTION)

        val result = fileCoordinator.generateFiles(ctx, context.psiFile, targetClass, progressCallback)

        progressCallback.updatePhase(ProgressTracker.GenerationPhase.FILE_CREATION)

        val processedFiles = fileProcessor.processAllFiles(context, ctx, result)

        progressCallback.updatePhase(ProgressTracker.GenerationPhase.FINALIZATION)

        processedFiles.lastOrNull()?.let { lastFile ->
            navigator.navigateToClass(context.project, lastFile)
            // Pass the supplier file (lastFile) to add methods to TestData class
            testDataMethodAdder.addMethodIfNeeded(context.project, ctx.testData, lastFile)
        }

        progressCallback.setDetailText("Generation completed successfully")
    }

    // Overload for backward compatibility
    fun perform(
        context: ActionContext,
        targetClass: PsiClass,
        options: DialogOptions,
        targetDir: PsiDirectory
    ) {
        // Create a no-op progress callback for backward compatibility
        val noOpCallback = object : ProgressCallback {
            override fun updatePhase(phase: ProgressTracker.GenerationPhase) {}
            override fun incrementField(fieldName: String) {}
            override fun setDetailText(text: String) {}
            override fun checkCanceled() {}
            override fun isCanceled(): Boolean = false
        }
        perform(context, targetClass, options, targetDir, noOpCallback)
    }
}
