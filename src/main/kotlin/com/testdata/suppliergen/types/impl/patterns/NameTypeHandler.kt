package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for name fields based on field name patterns.
 * Matches fields like: firstName, lastName, fullName, name, userName, etc.
 */
object NameTypeHandler : PatternBasedTypeHandler {

    private val namePatterns = listOf(
        "name", "firstname", "first_name", "lastname", "last_name",
        "fullname", "full_name", "username", "user_name", "displayname",
        "display_name", "nickname", "nick_name", "title", "surname"
    )

    private val firstNames = listOf(
        "John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Lisa",
        "James", "Mary", "William", "Jennifer", "Richard", "Patricia", "Charles",
        "Linda", "Thomas", "Barbara", "Christopher", "Elizabeth", "Daniel", "Helen",
        "Matthew", "Sandra", "Anthony", "Donna", "Mark", "Carol", "Donald", "Ruth"
    )

    private val lastNames = listOf(
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
        "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson",
        "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson"
    )

    override val priority: Int = 7

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, namePatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("first") -> "\"${firstNames.random()}\""
            fieldName.lowercase().contains("last") || fieldName.lowercase().contains("surname") -> "\"${lastNames.random()}\""
            fieldName.lowercase().contains("full") || fieldName.lowercase().contains("display") -> {
                "\"${firstNames.random()} ${lastNames.random()}\""
            }
            fieldName.lowercase().contains("user") || fieldName.lowercase().contains("nick") -> {
                "\"${firstNames.random().lowercase()}${(1..999).random()}\""
            }
            else -> "\"${firstNames.random()}\""
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("first") -> "\"${firstNames.random()}\""
            fieldName.lowercase().contains("last") || fieldName.lowercase().contains("surname") -> "\"${lastNames.random()}\""
            fieldName.lowercase().contains("full") || fieldName.lowercase().contains("display") -> {
                "\"${firstNames.random()} ${lastNames.random()}\""
            }
            fieldName.lowercase().contains("user") || fieldName.lowercase().contains("nick") -> {
                "\"${firstNames.random().lowercase()}${(1..999).random()}\""
            }
            else -> "\"${firstNames.random()}\""
        }
    }

    override val staticExtraImports: Set<String> = emptySet()
}