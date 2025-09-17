package com.testdata.suppliergen.types.contract

import com.intellij.psi.PsiType

/**
 * Interface for type handlers that match fields based on field name patterns
 * rather than just the field type. Useful for generating realistic test data
 * based on field semantics (e.g., email, ssn, iban, etc.)
 */
interface PatternBasedTypeHandler : TypeHandler {
    /**
     * Check if this handler can handle a field based on its name pattern
     * @param fieldName The name of the field (e.g., "email", "userEmail", "customer_email")
     * @param fqName The fully qualified type name (usually String for pattern-based handlers)
     * @param psiType The PSI type information
     * @return true if this handler should process this field
     */
    fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean

    /**
     * Get the priority of this handler. Higher priority handlers are checked first.
     * This allows more specific patterns to override general ones.
     * @return priority value (higher = more priority)
     */
    val priority: Int get() = 0

    /**
     * Pattern matching is case-insensitive by default
     */
    fun matchesPattern(fieldName: String, patterns: List<String>): Boolean {
        val lowerFieldName = fieldName.lowercase()
        return patterns.any { pattern ->
            lowerFieldName.contains(pattern.lowercase()) ||
            lowerFieldName.matches(pattern.toRegex(RegexOption.IGNORE_CASE))
        }
    }
}