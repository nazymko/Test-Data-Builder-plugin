package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Type handler for personal information fields like age, birth date, etc.
 * Matches fields like: age, birthDate, dateOfBirth, dob, birthday
 */
object PersonalInfoTypeHandler : PatternBasedTypeHandler {

    private val agePatterns = listOf("age")
    private val birthDatePatterns = listOf(
        "birth", "dob", "dateofbirth", "date_of_birth", "birthday", "bday"
    )

    override val priority: Int = 13

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return when (fqName) {
            "java.lang.String", "String" -> true
            "java.lang.Integer", "Integer", "int" -> true
            "java.time.LocalDate", "LocalDate" -> true
            "java.util.Date", "Date" -> true
            else -> false
        }
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) &&
               (matchesPattern(fieldName, agePatterns) || matchesPattern(fieldName, birthDatePatterns))
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            matchesPattern(fieldName, agePatterns) -> generateAge(fqName)
            matchesPattern(fieldName, birthDatePatterns) -> generateBirthDate(fqName)
            else -> "null"
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            matchesPattern(fieldName, agePatterns) -> generateAge(fqName)
            matchesPattern(fieldName, birthDatePatterns) -> generateBirthDate(fqName)
            else -> "null"
        }
    }

    private fun generateAge(fqName: String?): String {
        val age = (18..75).random()
        return when (fqName) {
            "java.lang.String", "String" -> "\"$age\""
            else -> "$age"
        }
    }

    private fun generateBirthDate(fqName: String?): String {
        val currentYear = LocalDate.now().year
        val birthYear = (currentYear - 75)..(currentYear - 18)
        val month = (1..12).random()
        val day = (1..28).random() // Safe day range for all months

        return when (fqName) {
            "java.lang.String", "String" -> {
                "\"${String.format("%04d-%02d-%02d", birthYear.random(), month, day)}\""
            }
            "java.time.LocalDate", "LocalDate" -> {
                "java.time.LocalDate.of(${birthYear.random()}, $month, $day)"
            }
            "java.util.Date", "Date" -> {
                val calendar = "new java.util.GregorianCalendar(${birthYear.random()}, ${month - 1}, $day)"
                "$calendar.getTime()"
            }
            else -> "null"
        }
    }

    override val staticExtraImports: Set<String> = setOf(
        "java.time.LocalDate",
        "java.util.Date",
        "java.util.GregorianCalendar"
    )
}