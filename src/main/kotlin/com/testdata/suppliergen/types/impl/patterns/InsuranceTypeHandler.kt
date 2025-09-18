package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object InsuranceTypeHandler : PatternBasedTypeHandler {
    private val insurancePatterns = listOf("insurance", "insurancenumber", "insurance_number", "policynumber", "policy_number", "memberid", "member_id")
    override val priority: Int = 19
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, insurancePatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = when {
        fieldName.lowercase().contains("policy") -> "\"POL-${('A'..'Z').random()}${('A'..'Z').random()}${('A'..'Z').random()}${(100000..999999).random()}\""
        fieldName.lowercase().contains("member") -> "\"MBR-${(1000000000L..9999999999L).random()}\""
        else -> "\"INS-${(1000000000L..9999999999L).random()}\""
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}