package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for address fields based on field name patterns.
 * Matches fields like: address, street, city, state, zipCode, postalCode, country, etc.
 */
object AddressTypeHandler : PatternBasedTypeHandler {

    private val addressPatterns = listOf(
        "address", "street", "city", "state", "zip", "postal", "country",
        "region", "province", "location", "residence", "home"
    )

    private val streetNames = listOf(
        "Main Street", "Oak Avenue", "Park Road", "First Street", "Second Avenue",
        "Elm Street", "Maple Avenue", "Pine Road", "Cedar Street", "Washington Avenue",
        "Lincoln Street", "Madison Avenue", "Jefferson Road", "Franklin Street", "Market Street"
    )

    private val cities = listOf(
        "New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia",
        "San Antonio", "San Diego", "Dallas", "San Jose", "Austin", "Jacksonville",
        "San Francisco", "Columbus", "Charlotte", "Indianapolis", "Seattle", "Denver"
    )

    private val states = listOf(
        "CA", "NY", "TX", "FL", "PA", "IL", "OH", "GA", "NC", "MI",
        "NJ", "VA", "WA", "AZ", "MA", "TN", "IN", "MO", "MD", "WI"
    )

    private val countries = listOf(
        "United States", "Canada", "United Kingdom", "Germany", "France",
        "Australia", "Japan", "Italy", "Spain", "Netherlands"
    )

    override val priority: Int = 6

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, addressPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("street") || fieldName.lowercase().contains("address") -> {
                val number = (100..9999).random()
                "\"$number ${streetNames.random()}\""
            }
            fieldName.lowercase().contains("city") -> "\"${cities.random()}\""
            fieldName.lowercase().contains("state") || fieldName.lowercase().contains("province") -> "\"${states.random()}\""
            fieldName.lowercase().contains("zip") || fieldName.lowercase().contains("postal") -> {
                "\"${(10000..99999).random()}\""
            }
            fieldName.lowercase().contains("country") -> "\"${countries.random()}\""
            else -> {
                val number = (100..9999).random()
                "\"$number ${streetNames.random()}\""
            }
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("street") || fieldName.lowercase().contains("address") -> {
                val number = (100..9999).random()
                "\"$number ${streetNames.random()}\""
            }
            fieldName.lowercase().contains("city") -> "\"${cities.random()}\""
            fieldName.lowercase().contains("state") || fieldName.lowercase().contains("province") -> "\"${states.random()}\""
            fieldName.lowercase().contains("zip") || fieldName.lowercase().contains("postal") -> {
                "\"${(10000..99999).random()}\""
            }
            fieldName.lowercase().contains("country") -> "\"${countries.random()}\""
            else -> {
                val number = (100..9999).random()
                "\"$number ${streetNames.random()}\""
            }
        }
    }

    override val staticExtraImports: Set<String> = emptySet()
}