package com.testdata.suppliergen.types.helpers

import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType

// OptionalTypeChecker.kt
class OptionalSupportChecker {

    fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return when {
            fqName != null -> isOptionalByFqName(fqName, psiType)
            psiType != null -> isOptionalByPsiType(psiType)
            else -> false
        }
    }

    private fun isOptionalByFqName(fqName: String, psiType: PsiType?): Boolean {
        return when {
            isOptionalFqNamePattern(fqName) -> true
            fqName == JAVA_UTIL_OPTIONAL -> isGenericOptional(psiType)
            else -> false
        }
    }

    private fun isOptionalByPsiType(psiType: PsiType): Boolean {
        if (psiType !is PsiClassType || psiType.parameters.size != 1) {
            return false
        }

        val baseFqName = psiType.canonicalText.substringBefore("<")
        return baseFqName == JAVA_UTIL_OPTIONAL || baseFqName == SIMPLE_OPTIONAL
    }

    private fun isOptionalFqNamePattern(fqName: String): Boolean {
        return fqName.matches(OPTIONAL_PATTERN) || fqName.matches(JAVA_UTIL_OPTIONAL_PATTERN)
    }

    private fun isGenericOptional(psiType: PsiType?): Boolean {
        return psiType is PsiClassType && psiType.parameters.size == 1
    }

    companion object {
        private const val JAVA_UTIL_OPTIONAL = "java.util.Optional"
        private const val SIMPLE_OPTIONAL = "Optional"
        private val OPTIONAL_PATTERN = Regex("Optional<.*>")
        private val JAVA_UTIL_OPTIONAL_PATTERN = Regex("java\\.util\\.Optional<.*>")
    }
}
