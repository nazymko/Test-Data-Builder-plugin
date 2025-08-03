package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.helpers.EnumHelper.getEnumConstantsFromClass
import com.intellij.psi.PsiType

object EnumHandler : GenericEnumHandler() {

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        val psiClass = resolvePsiClass(fqName, psiType)
        check(psiType)

        if (psiClass == null) {
            return false
        }

        val isEnum = psiClass.isEnum
        return isEnum
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return randomizedValue(fieldName, fqName, psiType)
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val psiClass = resolvePsiClass(fqName, psiType)
        val constants = getEnumConstantsFromClass(psiClass)

        return if (constants.isNotEmpty()) {
            val index = (0 until constants.size).random()
            constants.getOrNull(index) ?: constants.first()
        } else {
            "null"
        }
    }


    override fun getterPrefix(fqName: String?, psiType: PsiType): String = "get"

}
