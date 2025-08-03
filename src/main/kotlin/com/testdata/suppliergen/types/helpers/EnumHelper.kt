package com.testdata.suppliergen.types.helpers

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiEnumConstant

object EnumHelper {

    public fun getEnumConstantsFromClass(psiClass: PsiClass?): List<String> {

        if (psiClass?.isEnum != true) return emptyList()

        return psiClass.fields
            .filterIsInstance<PsiEnumConstant>()
            .map { "${psiClass.qualifiedName}.${it.name}" }
    }
}