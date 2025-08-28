package com.testdata.suppliergen.v2

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager

// 12. Code Style Formatter
class CodeStyleFormatter {
    private val logger = Logger.getInstance(CodeStyleFormatter::class.java)

    fun formatFile(project: Project, psiFile: PsiJavaFile, originalFile: PsiJavaFile) {
        try {
            val manager = JavaCodeStyleManager.getInstance(project)
            val style = CodeStyleManager.getInstance(project)
            
            // These are safe to run in write action
            manager.shortenClassReferences(psiFile)
            style.reformat(psiFile)
            
            // Skip OptimizeImportsProcessor as it can trigger dialogs in write action
            // The imports should be handled by the generation process itself
            logger.debug("Formatted file: ${psiFile.name}")
            
        } catch (e: Exception) {
            logger.warn("Failed to format file ${psiFile.name}: ${e.message}")
            // Don't rethrow - formatting is not critical for functionality
        }
    }
}