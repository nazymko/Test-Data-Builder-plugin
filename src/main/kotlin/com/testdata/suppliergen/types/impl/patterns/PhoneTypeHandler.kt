package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for phone number fields based on field name patterns.
 * Matches fields like: phone, telephone, mobile, cell, contact, phoneNumber, etc.
 */
object PhoneTypeHandler : PatternBasedTypeHandler {

    private val phonePatterns = listOf(
        "phone", "telephone", "mobile", "cell", "contact",
        "number", "tel", "fax", "cellular"
    )

    private val areaCodes = listOf("555", "123", "456", "789", "234", "345", "678", "890")

    override val priority: Int = 14

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, phonePatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("mobile") || fieldName.lowercase().contains("cell") ->
                generateMobileFormat()
            fieldName.lowercase().contains("fax") ->
                generateFaxFormat()
            else -> generateStandardFormat()
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("mobile") || fieldName.lowercase().contains("cell") ->
                generateMobileFormat()
            fieldName.lowercase().contains("fax") ->
                generateFaxFormat()
            else -> generateStandardFormat()
        }
    }

    private fun generateStandardFormat(): String {
        val areaCode = areaCodes.random()
        val exchange = (200..999).random()
        val number = (1000..9999).random()
        return "\"($areaCode) $exchange-$number\""
    }

    private fun generateMobileFormat(): String {
        val areaCode = areaCodes.random()
        val exchange = (200..999).random()
        val number = (1000..9999).random()
        return "\"+1-$areaCode-$exchange-$number\""
    }

    private fun generateFaxFormat(): String {
        val areaCode = areaCodes.random()
        val exchange = (200..999).random()
        val number = (1000..9999).random()
        return "\"$areaCode.$exchange.$number\""
    }

    override val staticExtraImports: Set<String> = emptySet()
}