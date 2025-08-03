package com.testdata.suppliergen.v2

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

// 14. Error Handler
class ErrorHandler {
    private val logger = Logger.getInstance(GenerateSupplierAction::class.java)
    
    fun handleError(project: Project?, message: String, exception: Exception? = null) {
        exception?.let { logger.error(message, it) }
        Messages.showErrorDialog(project, exception?.message ?: message, "Plugin Error")
    }
}
