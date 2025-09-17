package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object IPAddressTypeHandler : PatternBasedTypeHandler {
    private val ipPatterns = listOf("ip", "ipaddress", "ip_address", "server", "host")
    override val priority: Int = 5
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, ipPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"192.168.${(1..255).random()}.${(1..254).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"10.0.${(1..255).random()}.${(1..254).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}