package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType


object LocalDateHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.time.LocalDate", "LocalDate")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String  {
        val now = java.time.LocalDate.now()
        return "java.time.LocalDate.of(${now.year}, ${now.monthValue}, ${now.dayOfMonth})"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return defaultValue(fieldName, fqName, psiType)
    }

    override val staticExtraImports: Set<String> get() = setOf("java.time.LocalDate")
}
