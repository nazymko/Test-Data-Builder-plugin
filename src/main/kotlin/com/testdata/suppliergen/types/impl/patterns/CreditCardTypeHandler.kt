package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for credit card fields based on field name patterns.
 * Matches fields like: creditCard, card, cardNumber, cc, etc.
 * Generates test credit card numbers that follow format but are not real.
 */
object CreditCardTypeHandler : PatternBasedTypeHandler {

    private val cardPatterns = listOf(
        "credit", "card", "cardnumber", "card_number", "cc", "payment"
    )

    // Test credit card prefixes (not real cards)
    private val visaTestPrefixes = listOf("4532", "4556", "4716", "4929")
    private val mastercardTestPrefixes = listOf("5555", "5105", "5200", "5400")
    private val amexTestPrefixes = listOf("3434", "3782", "3714")

    override val priority: Int = 16

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, cardPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return generateTestCreditCard()
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return generateTestCreditCard()
    }

    private fun generateTestCreditCard(): String {
        val cardType = (1..3).random()

        return when (cardType) {
            1 -> generateVisa()
            2 -> generateMastercard()
            else -> generateAmex()
        }.let { "\"$it\"" }
    }

    private fun generateVisa(): String {
        val prefix = visaTestPrefixes.random()
        val suffix = (100000000000L..999999999999L).random()
        return "$prefix$suffix".take(16)
    }

    private fun generateMastercard(): String {
        val prefix = mastercardTestPrefixes.random()
        val suffix = (100000000000L..999999999999L).random()
        return "$prefix$suffix".take(16)
    }

    private fun generateAmex(): String {
        val prefix = amexTestPrefixes.random()
        val suffix = (10000000000L..99999999999L).random()
        return "$prefix$suffix".take(15)
    }

    override val staticExtraImports: Set<String> = emptySet()
}