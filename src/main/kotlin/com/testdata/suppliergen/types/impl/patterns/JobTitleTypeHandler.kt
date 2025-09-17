package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for job title fields based on field name patterns.
 * Matches fields like: job, title, position, role, occupation, jobTitle, etc.
 */
object JobTitleTypeHandler : PatternBasedTypeHandler {

    private val jobPatterns = listOf(
        "job", "title", "position", "role", "occupation",
        "designation", "rank", "level"
    )

    private val techJobs = listOf(
        "Software Engineer", "Senior Developer", "Technical Lead", "DevOps Engineer",
        "Data Scientist", "Product Manager", "UI/UX Designer", "QA Engineer",
        "System Administrator", "Database Administrator", "Security Analyst"
    )

    private val businessJobs = listOf(
        "Sales Manager", "Marketing Director", "Business Analyst", "Account Manager",
        "HR Director", "Financial Analyst", "Operations Manager", "Project Manager",
        "Customer Success Manager", "Business Development Manager"
    )

    private val generalJobs = listOf(
        "Manager", "Director", "Analyst", "Coordinator", "Specialist",
        "Executive", "Assistant", "Administrator", "Consultant", "Associate"
    )

    override val priority: Int = 9

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, jobPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("tech") ||
            fieldName.lowercase().contains("engineer") ||
            fieldName.lowercase().contains("developer") ->
                "\"${techJobs.random()}\""
            fieldName.lowercase().contains("business") ||
            fieldName.lowercase().contains("sales") ||
            fieldName.lowercase().contains("marketing") ->
                "\"${businessJobs.random()}\""
            else ->
                "\"${(techJobs + businessJobs + generalJobs).random()}\""
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("tech") ||
            fieldName.lowercase().contains("engineer") ||
            fieldName.lowercase().contains("developer") ->
                "\"${techJobs.random()}\""
            fieldName.lowercase().contains("business") ||
            fieldName.lowercase().contains("sales") ||
            fieldName.lowercase().contains("marketing") ->
                "\"${businessJobs.random()}\""
            else ->
                "\"${(techJobs + businessJobs + generalJobs).random()}\""
        }
    }

    override val staticExtraImports: Set<String> = emptySet()
}