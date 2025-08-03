package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object ZoneIdHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.time.ZoneId", "ZoneId")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "java.time.ZoneId.of(\"UTC\")"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> get() = setOf("java.time.ZoneId")
}
