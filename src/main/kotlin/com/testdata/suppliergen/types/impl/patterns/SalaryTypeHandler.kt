package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for salary/wage fields based on field name patterns.
 * Matches fields like: salary, wage, income, compensation, pay, etc.
 */
object SalaryTypeHandler : PatternBasedTypeHandler {

    private val salaryPatterns = listOf(
        "salary", "wage", "income", "compensation", "pay",
        "earnings", "remuneration", "stipend"
    )

    override val priority: Int = 9

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return when (fqName) {
            "java.lang.String", "String" -> true
            "java.math.BigDecimal", "BigDecimal" -> true
            "java.lang.Double", "Double", "double" -> true
            "java.lang.Float", "Float", "float" -> true
            "java.lang.Long", "Long", "long" -> true
            "java.lang.Integer", "Integer", "int" -> true
            else -> false
        }
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, salaryPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val salary = when {
            fieldName.lowercase().contains("hourly") || fieldName.lowercase().contains("wage") ->
                (15..75).random() // Hourly wage
            else -> (35000..150000).random() // Annual salary
        }

        return when (fqName) {
            "java.lang.String", "String" -> "\"$$salary\""
            "java.math.BigDecimal", "BigDecimal" -> "new java.math.BigDecimal($salary)"
            "java.lang.Double", "Double", "double" -> "$salary.0"
            "java.lang.Float", "Float", "float" -> "${salary}.0f"
            "java.lang.Long", "Long", "long" -> "${salary}L"
            else -> "$salary"
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val salary = when {
            fieldName.lowercase().contains("hourly") || fieldName.lowercase().contains("wage") ->
                (15..75).random() // Hourly wage
            else -> (35000..150000).random() // Annual salary
        }

        return when (fqName) {
            "java.lang.String", "String" -> "\"$$salary\""
            "java.math.BigDecimal", "BigDecimal" -> "new java.math.BigDecimal($salary)"
            "java.lang.Double", "Double", "double" -> "$salary.0"
            "java.lang.Float", "Float", "float" -> "${salary}.0f"
            "java.lang.Long", "Long", "long" -> "${salary}L"
            else -> "$salary"
        }
    }

    override val staticExtraImports: Set<String> = setOf("java.math.BigDecimal")
}