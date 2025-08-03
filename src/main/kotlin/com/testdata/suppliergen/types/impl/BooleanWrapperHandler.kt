package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType


object BooleanWrapperHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.lang.Boolean", "Boolean")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "false"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)

    override fun getterPrefix(fqName: String?, psiType: PsiType): String = "get"

    override val staticExtraImports: Set<String> get() = setOf()
}
