package com.testdata.suppliergen.v2

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiJavaFile

// 3. Context Extractor
class ActionContextExtractor {
    fun extractContext(e: AnActionEvent): ActionContext? {
        val project = e.project ?: return null
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile ?: return null
        
        return ActionContext(project, editor, psiFile)
    }
}