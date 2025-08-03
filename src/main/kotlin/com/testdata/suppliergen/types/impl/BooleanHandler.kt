package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object BooleanHandler : TypeHandler {
    override val supportedTypes: Set<String> get() = setOf("boolean")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "false"
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)

    override fun getterPrefix(fqName: String?, psiType: PsiType) = "is"
}