package com.testdata.suppliergen.v2

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile

// 2. Action State Validation
class ActionStateValidator {
    fun updateActionState(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        
        e.presentation.isEnabledAndVisible = 
            editor != null && (file is PsiJavaFile || psiElement is PsiClass)
    }
}
