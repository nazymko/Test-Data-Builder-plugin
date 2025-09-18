package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import kotlin.random.Random

object CoordinateTypeHandler : PatternBasedTypeHandler {
    private val coordPatterns = listOf("latitude", "longitude", "lat", "lng", "coord")
    override val priority: Int = 6
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String", "java.lang.Double", "Double", "double", "java.lang.Float", "Float", "float" -> true
        else -> false
    }
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, coordPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val coord = if (fieldName.lowercase().contains("lat")) {
            Random.nextDouble(25.0, 49.0) // US latitude range
        } else {
            Random.nextDouble(-125.0, -67.0) // US longitude range
        }
        val formattedCoord = String.format("%.6f", coord)
        return when (fqName) {
            "java.lang.String", "String" -> "\"$formattedCoord\""
            "java.lang.Float", "Float", "float" -> "${formattedCoord}f"
            else -> formattedCoord
        }
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}