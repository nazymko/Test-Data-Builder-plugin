package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object DurationHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.time.Duration", "Duration")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "java.time.Duration.ofSeconds(60)"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> get() = setOf("java.time.Duration", )
}
