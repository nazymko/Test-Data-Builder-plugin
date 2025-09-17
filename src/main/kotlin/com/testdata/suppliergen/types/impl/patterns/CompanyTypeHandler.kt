package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for company name fields based on field name patterns.
 * Matches fields like: company, employer, organization, business, companyName, etc.
 */
object CompanyTypeHandler : PatternBasedTypeHandler {

    private val companyPatterns = listOf(
        "company", "employer", "organization", "business",
        "corporation", "enterprise", "firm", "org"
    )

    private val techCompanies = listOf(
        "Tech Solutions Inc", "Digital Innovations LLC", "Cloud Systems Corp",
        "Data Analytics Inc", "Software Solutions LLC", "Innovation Labs Corp",
        "Cyber Security Inc", "AI Technologies LLC", "Platform Solutions Corp"
    )

    private val businessCompanies = listOf(
        "Global Industries Inc", "Business Solutions LLC", "Enterprise Corp",
        "Strategic Partners Inc", "Professional Services LLC", "Consulting Group Corp",
        "Management Solutions Inc", "Corporate Services LLC", "Business Partners Corp"
    )

    private val companyPrefixes = listOf(
        "Advanced", "Global", "Premier", "Elite", "Dynamic", "Strategic",
        "Professional", "Innovative", "Superior", "Leading"
    )

    private val companySuffixes = listOf(
        "Inc", "LLC", "Corp", "Ltd", "Group", "Partners", "Associates", "Solutions"
    )

    override val priority: Int = 8

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, companyPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("tech") ||
            fieldName.lowercase().contains("software") ||
            fieldName.lowercase().contains("it") ->
                "\"${techCompanies.random()}\""
            else -> generateRandomCompany()
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("tech") ||
            fieldName.lowercase().contains("software") ||
            fieldName.lowercase().contains("it") ->
                "\"${techCompanies.random()}\""
            else -> generateRandomCompany()
        }
    }

    private fun generateRandomCompany(): String {
        return if ((1..3).random() == 1) {
            // Use predefined company names
            (techCompanies + businessCompanies).random()
        } else {
            // Generate new company name
            val prefix = companyPrefixes.random()
            val suffix = companySuffixes.random()
            "$prefix $suffix"
        }.let { "\"$it\"" }
    }

    override val staticExtraImports: Set<String> = emptySet()
}