package com.testdata.suppliergen.v2

import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiTreeUtil

// 4. Target Class Resolver
class TargetClassResolver {
    fun resolveTargetClass(context: ActionContext): PsiClass? {
        val caretOffset = context.editor.caretModel.offset
        val elementAtCaret = context.psiFile.findElementAt(caretOffset)
        val targetClass = elementAtCaret?.let {
            PsiTreeUtil.getParentOfType(it, PsiClass::class.java)
        }
        return targetClass ?: context.psiFile.classes.firstOrNull()
    }
}
