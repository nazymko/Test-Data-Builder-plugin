package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType


object LocalDateTimeHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.time.LocalDateTime", "LocalDateTime")


    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val now = java.time.LocalDateTime.now()
        return "java.time.LocalDateTime.of(${now.year}, ${now.monthValue}, ${now.dayOfMonth}, ${now.hour}, ${now.minute})"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return defaultValue(fieldName, fqName, psiType)
    }

    override val staticExtraImports: Set<String> get() = setOf("java.time.LocalDateTime")
}
