package com.testdata.suppliergen.v2.v3

import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.v2.ActionContext
import com.testdata.suppliergen.v2.DialogOptions
import com.testdata.suppliergen.v2.DirectoryManager
import com.testdata.suppliergen.v2.ErrorHandler
import com.testdata.suppliergen.progress.ProgressIndicatorManager
import com.testdata.suppliergen.progress.ProgressTracker
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiClass

// Handles the execution of generation with write command action
class GenerationExecutor {
    private val directoryManager = DirectoryManager()
    private val generationPerformer = GenerationPerformer()
    private val errorHandler = ErrorHandler()
    private val progressManager = ProgressIndicatorManager()

    fun execute(context: ActionContext, targetClass: PsiClass, options: DialogOptions) {
        TypeHandlerRegistry.clearCache() // Clear the type handler cache before generation

        val generateInTest = options.selectedSourceRoot.startsWith("Test")
        val targetDir = directoryManager.getTargetDirectory(context.psiFile, generateInTest)

        if (targetDir == null) {
            errorHandler.handleError(
                context.project,
                "Could not determine directory for package '${context.psiFile.packageName}'"
            )
            return
        }

        // Calculate total fields for progress tracking
        val progressTracker = ProgressTracker()
        val totalFields = progressTracker.calculateTotalFields(targetClass)

        // Execute with progress tracking
        progressManager.executeWithProgress(
            project = context.project,
            title = "Generating Supplier for ${targetClass.name}",
            totalFields = totalFields
        ) { progressCallback ->

            progressCallback.updatePhase(ProgressTracker.GenerationPhase.INITIALIZATION)

            // Execute write operations within WriteCommandAction
            WriteCommandAction.writeCommandAction(context.project)
                .withName("Generate Supplier")
                .withGroupId("SupplierGenerator")
                .run<Throwable> {
                    // Create a write-safe progress callback for operations inside write action
                    val writeCallback = progressManager.createWriteOperationCallback(totalFields)

                    // Perform generation with progress tracking
                    generationPerformer.perform(context, targetClass, options, targetDir, writeCallback)
                }
        }
    }
}
