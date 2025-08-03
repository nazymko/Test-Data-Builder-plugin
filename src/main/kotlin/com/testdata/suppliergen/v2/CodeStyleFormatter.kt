package com.testdata.suppliergen.v2

import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager

// 12. Code Style Formatter
class CodeStyleFormatter {
    fun formatFile(project: Project, psiFile: PsiJavaFile, originalFile: PsiJavaFile) {
        val manager = JavaCodeStyleManager.getInstance(project)
        val style = CodeStyleManager.getInstance(project)
        
        manager.shortenClassReferences(psiFile)
        style.reformat(psiFile)
        OptimizeImportsProcessor(project, originalFile).run()
    }
}