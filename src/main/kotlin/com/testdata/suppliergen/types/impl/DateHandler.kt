package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object DateHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.util.Date", "Date")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val now = java.time.Instant.now()
        return "new java.util.Date(java.time.Instant.parse(\"$now\").toEpochMilli())"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return defaultValue(fieldName, fqName, psiType)
    }

    override val staticExtraImports: Set<String> get() = setOf("java.util.Date")
}
