package com.testdata.suppliergen.v2.v3

import com.testdata.suppliergen.v2.*
import com.intellij.openapi.actionSystem.AnActionEvent

// Main processor - orchestrates the generation flow
class SupplierGenerationProcessor {
    private val contextExtractor = ActionContextExtractor()
    private val classResolver = TargetClassResolver()
    private val optionsHandler = OptionsDialogHandler()
    private val generationExecutor = GenerationExecutor()
    private val errorHandler = ErrorHandler()

    fun processAction(e: AnActionEvent) {
        try {
            val context = contextExtractor.extractContext(e) ?: return
            val targetClass = classResolver.resolveTargetClass(context)
            
            if (targetClass == null) {
                errorHandler.handleError(context.project, "No Java class found in file.")
                return
            }
            
            val options = optionsHandler.getOptions(context.project) ?: return
            generationExecutor.execute(context, targetClass, options)
        } catch (ex: Exception) {
            errorHandler.handleError(e.project, "Exception in GenerateSupplierAction", ex)
        }
    }
}
