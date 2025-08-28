package com.testdata.suppliergen.v2

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

// 14. Error Handler
class ErrorHandler {
    private val logger = Logger.getInstance(ErrorHandler::class.java)
    
    fun handleError(project: Project?, message: String, exception: Exception? = null) {
        exception?.let { 
            logger.error(message, it) 
        } ?: logger.error(message)
        
        // Schedule dialog display outside of write action to avoid threading violations
        ApplicationManager.getApplication().invokeLater {
            if (project != null && !project.isDisposed) {
                Messages.showErrorDialog(project, exception?.message ?: message, "Supplier Generator Error")
            }
        }
    }
    
    fun logError(message: String, exception: Exception? = null) {
        // For cases where we only want to log without showing dialog
        exception?.let { 
            logger.error(message, it) 
        } ?: logger.error(message)
    }
}
