package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType
import java.util.*

object UUIDHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.util.UUID", "UUID")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "UUID.fromString(\"${UUID.randomUUID()}\")";
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports = setOf("java.util.UUID")
}