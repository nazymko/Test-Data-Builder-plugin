package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for Social Security Number fields based on field name patterns.
 * Matches fields like: ssn, socialSecurityNumber, social_security_number, etc.
 * Generates test SSNs that follow the format but are not real/valid SSNs.
 */
object SSNTypeHandler : PatternBasedTypeHandler {

    private val ssnPatterns = listOf(
        "ssn", "social.*security", "socialsecurity", "social_security",
        "taxid", "tax_id", "nationalid", "national_id"
    )

    override val priority: Int = 15

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        // Handle String types for SSN
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, ssnPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        // Generate a test SSN format: XXX-XX-XXXX
        // Using 900+ area numbers which are not assigned to avoid real SSNs
        val area = (900..999).random()
        val group = (10..99).random()
        val serial = (1000..9999).random()
        return "\"$area-$group-$serial\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        // Generate different test SSN
        val area = (900..999).random()
        val group = (10..99).random()
        val serial = (1000..9999).random()
        return "\"$area-$group-$serial\""
    }

    override val staticExtraImports: Set<String> = emptySet()
}