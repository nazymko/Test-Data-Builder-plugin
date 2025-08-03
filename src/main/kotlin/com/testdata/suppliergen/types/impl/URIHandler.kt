package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object URIHandler : TypeHandler {

    override val supportedTypes: Set<String> get() = setOf("java.net.URI", "URI")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "java.net.URI.create(\"https://testdata.com\")"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> get() = setOf("java.net.URI")
}
