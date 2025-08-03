package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object InstantHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.time.Instant", "Instant")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val now = java.time.Instant.now()
        // Return Instant.parse with ISO-8601 string representation
        return "java.time.Instant.parse(\"$now\")"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> get() = setOf("java.time.Instant")
}
