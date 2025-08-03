package com.testdata.suppliergen.types.impl

import com.intellij.psi.PsiType
import com.testdata.suppliergen.types.contract.TypeHandler
import java.util.concurrent.atomic.AtomicInteger

object StringHandler : TypeHandler {
    private val counter = AtomicInteger(1) // start from 1

    override val supportedTypes: Set<String> get() = setOf("java.lang.String", "String")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return generateValue(fieldName) { "\"$it\"" }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return generateValue(fieldName) { "\"${it}_${counter.andIncrement}\"" }
    }

    private fun generateValue(fieldName: String, formatter: (String) -> String): String {
        val value = if (fieldName.lowercase().contains("iban")) randomIban() else fieldName
        return formatter(value)
    }

    private fun randomIban(): String {
        // Simple IBAN generation logic for demonstration purposes
        val countryCode = "DE" // Germany
        val bankCode = "50010517" // Example bank code
        val accountNumber = String.format("%010d", (1..9999999999).random()) // Random 10-digit account number
        val checksum = "00" // Placeholder for checksum, usually calculated based on the IBAN

        return "$countryCode$checksum$bankCode$accountNumber"
    }
}