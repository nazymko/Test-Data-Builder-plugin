package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.contract.TypeHandler
import com.testdata.suppliergen.types.helpers.OptionalSupportChecker
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType

object OptionalHandler : TypeHandler {
    private val optionalChecker = OptionalSupportChecker()

    override fun supports(fqName: String?, psiType: PsiType?): Boolean = optionalChecker.supports(fqName, psiType)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val innerTypeFqName = extractOptionalInnerType(fqName)
        val innerType = extractGenericType(psiType)
        val innerHandler = TypeHandlerRegistry.resolve(
            innerTypeFqName,
            innerType, "OptionalHandler defaultValue"
        )

        return if (innerHandler.isKnown) {
            // For known types, wrap with Optional
            val defaultInnerValue = innerHandler.defaultValue(fieldName, innerTypeFqName, innerType)
            "Optional.of($defaultInnerValue)"
        } else {
            // For unknown types, return unwrapped builder (will be wrapped in get method)
            "${innerTypeFqName ?: "Object"}Supplier.configuredBuilder()"
        }
    }

    fun extractOptionalInnerType(fqName: String?): String? {
        if (fqName == null) return null

        val cleaned = fqName.removePrefix("java.util.").trim()
        if (!cleaned.startsWith("Optional<") || !cleaned.endsWith(">")) return null

        return cleaned.substringAfter("Optional<").substringBeforeLast(">").trim()
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val innerType = extractGenericType(psiType)
        val innerFqName = (innerType as? PsiClassType)?.resolve()?.qualifiedName
        val innerHandler = TypeHandlerRegistry.resolve(innerFqName, innerType, "OptionalHandler randomizedValue")

        return if (innerHandler.isKnown) {
            // For known types, wrap with Optional
            val innerValue = innerHandler.randomizedValue(fieldName, innerFqName, innerType)
            "Optional.ofNullable($innerValue)"
        } else {
            // For unknown types, return unwrapped builder (will be wrapped in get method)
            "${innerFqName ?: "Object"}Supplier.configuredBuilder()"
        }
    }

    private fun extractGenericType(psiType: PsiType?): PsiType {
        if (psiType is PsiClassType) {
            val parameters = psiType.parameters
            if (parameters.size == 1) {
                return parameters[0]
            }
        }
        error("Expected Optional<T> with one generic parameter.")
    }

    override val staticExtraImports: Set<String> get() = setOf("java.util.Optional")
}