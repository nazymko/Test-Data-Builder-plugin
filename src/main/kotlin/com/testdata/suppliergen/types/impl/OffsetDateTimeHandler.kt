package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType
import java.time.OffsetDateTime

object OffsetDateTimeHandler : TypeHandler {
    override val supportedTypes: Set<String> get() = setOf("java.time.OffsetDateTime", "OffsetDateTime")


    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
       defaultValue(fieldName, fqName, psiType)

    override fun defaultValue (
        fieldName: String,
        fqName: String?,
        psiType: PsiType?
    ): String {
        val now = OffsetDateTime.now()
        val javaCodeString = """
OffsetDateTime.of(${now.year}, ${now.month.value}, ${now.dayOfMonth}, ${now.hour}, ${now.minute}, ${now.second},0, ZoneOffset.UTC)
""".trimIndent()
        return javaCodeString
    }

    override val staticExtraImports = setOf("java.time.OffsetDateTime", "java.time.ZoneOffset")
}