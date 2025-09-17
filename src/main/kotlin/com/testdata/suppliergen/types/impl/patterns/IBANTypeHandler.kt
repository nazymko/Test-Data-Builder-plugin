package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for IBAN (International Bank Account Number) fields based on field name patterns.
 * Matches fields like: iban, bankAccount, bank_account, accountNumber, etc.
 * Generates test IBANs that follow the format but are not real/valid accounts.
 */
object IBANTypeHandler : PatternBasedTypeHandler {

    private val ibanPatterns = listOf(
        "iban", "bank.*account", "bankaccount", "bank_account",
        "account.*number", "accountnumber", "account_number",
        "bban", "international.*bank"
    )

    // Test country codes and bank codes (not real)
    private val testCountryCodes = listOf("GB", "DE", "FR", "ES", "IT", "NL", "BE", "AT")
    private val testBankCodes = listOf("TEST", "DEMO", "SMPL", "BANK", "MOCK")

    override val priority: Int = 12

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, ibanPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return generateTestIBAN()
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return generateTestIBAN()
    }

    private fun generateTestIBAN(): String {
        val countryCode = testCountryCodes.random()
        val checkDigits = (10..99).random()
        val bankCode = testBankCodes.random()
        val accountNumber = (100000000000L..999999999999L).random()

        return "\"$countryCode$checkDigits$bankCode$accountNumber\""
    }

    override val staticExtraImports: Set<String> = emptySet()
}