package com.testdata.suppliergen.types.impl.collections.helpers

import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.impl.collections.helpers.ElementInfo
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil

object CollectionInspector {
    fun inspect(psiType: PsiType): ElementInfo {
        val rawTypeFq = (psiType as? PsiClassType)?.resolve()?.qualifiedName ?: "java.util.ArrayList"
        val paramType = PsiUtil.extractIterableTypeParameter(psiType, false)
        val elementPresentable = paramType?.presentableText
        val elementFq = PsiUtil.resolveClassInClassTypeOnly(paramType ?: psiType)?.qualifiedName
        val elementIsKnown = elementFq != null && TypeHandlerRegistry.resolve(
            elementFq,
            paramType ?: psiType,
            "CollectionHandler inspect"
        ).isKnown

        return ElementInfo(
            rawType = rawTypeFq,
            elementPresentableType = elementPresentable,
            elementFqType = elementFq,
            elementIsKnown = elementIsKnown
        )
    }
}