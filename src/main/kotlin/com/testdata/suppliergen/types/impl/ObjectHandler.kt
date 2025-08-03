package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object ObjectHandler : TypeHandler {
    override val supportedTypes: Set<String> get() = setOf("java.lang.Object", "Object")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        "null"

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)
}