package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for gender fields based on field name patterns.
 * Matches fields like: gender, sex, etc.
 */
object GenderTypeHandler : PatternBasedTypeHandler {

    private val genderPatterns = listOf("gender", "sex")

    private val genderValues = listOf("Male", "Female", "Other")
    private val genderCodes = listOf("M", "F", "O")

    override val priority: Int = 11

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, genderPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("code") || fieldName.length == 1 ->
                "\"${genderCodes.random()}\""
            else ->
                "\"${genderValues.random()}\""
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("code") || fieldName.length == 1 ->
                "\"${genderCodes.random()}\""
            else ->
                "\"${genderValues.random()}\""
        }
    }

    override val staticExtraImports: Set<String> = emptySet()
}