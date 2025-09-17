package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for currency fields based on field name patterns.
 * Matches fields like: currency, currencyCode, money, price, amount, etc.
 * Handles both String (currency codes) and numeric types (amounts).
 */
object CurrencyTypeHandler : PatternBasedTypeHandler {

    private val currencyPatterns = listOf(
        "currency", "money", "price", "amount", "cost", "fee",
        "balance", "payment", "salary", "wage", "total"
    )

    private val currencyCodes = listOf(
        "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "SEK", "NOK", "DKK"
    )

    override val priority: Int = 8

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
        return supports(fqName, psiType) && matchesPattern(fieldName, currencyPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fqName == "java.lang.String" || fqName == "String" -> {
                if (fieldName.lowercase().contains("code") || fieldName.lowercase().contains("currency")) {
                    "\"${currencyCodes.random()}\""
                } else {
                    "\"$100.00\""
                }
            }
            fqName == "java.math.BigDecimal" || fqName == "BigDecimal" ->
                "new java.math.BigDecimal(\"100.00\")"
            fqName == "java.lang.Double" || fqName == "Double" || fqName == "double" ->
                "100.00"
            fqName == "java.lang.Float" || fqName == "Float" || fqName == "float" ->
                "100.00f"
            fqName == "java.lang.Long" || fqName == "Long" || fqName == "long" ->
                "10000L"
            fqName == "java.lang.Integer" || fqName == "Integer" || fqName == "int" ->
                "10000"
            else -> "100.00"
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fqName == "java.lang.String" || fqName == "String" -> {
                if (fieldName.lowercase().contains("code") || fieldName.lowercase().contains("currency")) {
                    "\"${currencyCodes.random()}\""
                } else {
                    val amount = (1..999).random()
                    val cents = (0..99).random()
                    "\"$$amount.$cents\""
                }
            }
            fqName == "java.math.BigDecimal" || fqName == "BigDecimal" -> {
                val amount = (1..999).random()
                val cents = (0..99).random()
                "new java.math.BigDecimal(\"$amount.$cents\")"
            }
            fqName == "java.lang.Double" || fqName == "Double" || fqName == "double" -> {
                val amount = (1..999).random()
                val cents = (0..99).random()
                "$amount.$cents"
            }
            fqName == "java.lang.Float" || fqName == "Float" || fqName == "float" -> {
                val amount = (1..999).random()
                val cents = (0..99).random()
                "${amount}.${cents}f"
            }
            fqName == "java.lang.Long" || fqName == "Long" || fqName == "long" -> {
                val amount = (1000..99999).random()
                "${amount}L"
            }
            fqName == "java.lang.Integer" || fqName == "Integer" || fqName == "int" -> {
                val amount = (1000..99999).random()
                "$amount"
            }
            else -> "100.00"
        }
    }

    override val staticExtraImports: Set<String> = setOf("java.math.BigDecimal")
}