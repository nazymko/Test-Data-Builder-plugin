package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object CoordinateTypeHandler : PatternBasedTypeHandler {
    private val coordPatterns = listOf("latitude", "longitude", "lat", "lng", "coord")
    override val priority: Int = 6
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String", "java.lang.Double", "Double", "double", "java.lang.Float", "Float", "float" -> true
        else -> false
    }
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, coordPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val coord = if (fieldName.lowercase().contains("lat")) (25.0 + Math.random() * 24.0) else (-125.0 + Math.random() * 58.0)
        return when (fqName) {
            "java.lang.String", "String" -> "String.format(\"%.6f\", $coord)"
            "java.lang.Float", "Float", "float" -> "${coord}f"
            else -> "$coord"
        }
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}