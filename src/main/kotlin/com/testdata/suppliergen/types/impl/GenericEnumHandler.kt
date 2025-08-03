package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType

abstract class GenericEnumHandler : TypeHandler {

    fun isEnumType(psiType: PsiType?): Boolean {
        val psiClass = resolvePsiClass(null, psiType)
        return psiClass?.isEnum ?: false
    }

    fun isNestedEnum(psiType: PsiType?): Boolean {
        val psiClass = (psiType as? PsiClassType)?.resolve()
        return psiClass?.isEnum == true
    }

    fun check(psiType: PsiType?): Boolean {
        val psiClass = (psiType as? PsiClassReferenceType)?.resolve()

        val tr = psiClass
        return psiClass?.isEnum == true
    }

    fun isEnumFromPsiType(psiType: PsiType?): Boolean {
        if (psiType !is PsiClassType) return false

        val psiClass = psiType.resolve() ?: return false

        // psiClass.qualifiedName will be something like "com.testdata.D10.TestEnum"
        println("Resolved qualified name: ${psiClass.qualifiedName}")

        return psiClass.isEnum
    }

    override fun getterPrefix(fqName: String?, psiType: PsiType): String = "get"

}
