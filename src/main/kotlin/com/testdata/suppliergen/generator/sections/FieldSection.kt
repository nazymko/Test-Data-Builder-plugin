package com.testdata.suppliergen.generator.sections

import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.model.SupplierClassModel
import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType

class FieldSection(private val useFqNames: Boolean = true) : SectionBuilder {

    override fun render(model: SupplierClassModel): String {
        return model.fields.joinToString("\n", prefix = "\n") { renderField(it) } + "\n"
    }

    private fun renderField(field: FieldModel): String {
        return when {
            field.isOptional && field.isKnown -> renderKnownOptionalField(field)
            field.isOptional && !field.isKnown -> renderUnknownOptionalField(field)
            field.isCollection && field.elementIsKnown -> renderKnownCollectionField(field)
            field.isCollection && !field.elementIsKnown -> renderUnknownCollectionField(field)
            field.isMap -> renderMapField(field) // Simplified to single map handling method
            !field.isKnown -> renderUnknownField(field)
            else -> renderKnownField(field)
        }
    }

    private fun renderKnownField(field: FieldModel): String {
        val type = if (useFqNames) field.fqType else field.type
        return "    private $type ${field.name};"
    }

    private fun renderUnknownField(field: FieldModel): String {
        val type = if (useFqNames) field.fqType else field.type
        val builderType = "${type}Supplier.${field.type}SupplierBuilder"
        return "    private $builderType ${field.name};"
    }

    private fun renderKnownOptionalField(field: FieldModel): String {
        val innerType =
            if (useFqNames) field.optionalInnerFqType ?: field.optionalInnerType else field.optionalInnerType
        return "    private Optional<$innerType> ${field.name};"
    }

    private fun renderUnknownOptionalField(field: FieldModel): String {
        val innerType =
            if (useFqNames) field.optionalInnerFqType ?: field.optionalInnerType else field.optionalInnerType
        val builderType = "${innerType}Supplier.${innerType}SupplierBuilder"
        return "    private Optional<$builderType> ${field.name};"
    }

    private fun renderKnownCollectionField(field: FieldModel): String {
        val type = if (useFqNames) field.fqType else field.type
        return "    private $type ${field.name};"
    }

    private fun renderUnknownCollectionField(field: FieldModel): String {
        val raw = field.collectionRawType ?: "java.util.List"
        val elemType =
            if (useFqNames) field.elementFqType ?: field.elementPresentableType else field.elementPresentableType
        val builderType = "${elemType}Supplier.${field.elementPresentableType}SupplierBuilder"
        return "    private $raw<$builderType> ${field.name};"
    }

    /**
     * Enhanced map field rendering that handles nested generic types
     */
    private fun renderMapField(field: FieldModel): String {
        val mapType = if (useFqNames) field.mapRawType ?: "java.util.Map" else "Map"

        // Process key type (can be simple or complex)
        val keyType = processType(field.keyType, field.keyPsiType, field.keyIsKnown, field)

        // Process value type (can be simple or complex)
        val valueType = processType(field.valueType, field.valuePsiType, field.valueIsKnown,field)

        return "    private $mapType<$keyType, $valueType> ${field.name};"
    }

    /**
     * Process a type (key or value) and determine if it should use SupplierBuilder or original type
     */
    private fun processType(typeString: String?, psiType: PsiType?, isKnown: Boolean?, model: FieldModel): String {
        // Handle null cases
        if (typeString == null) return "Object"

        // If it's a known type, return as-is
        if (isKnown == true) {
            return typeString
        }

        // Check if this is a complex generic type (contains < and >)
        if (typeString.contains('<') && typeString.contains('>')) {
            return processGenericType(typeString, psiType, model)
        }

        // For simple unknown types, create SupplierBuilder
        if (isKnown == false) {
            val simpleTypeName = typeString.substringAfterLast('.')
            return "${typeString}Supplier.${simpleTypeName}SupplierBuilder"
        }

        // Default case - check with TypeHandlerRegistry if available
        return if (psiType != null) {
            val handler = TypeHandlerRegistry.resolve(typeString, psiType, "FieldSection processType")
            if (handler.isKnown) {
                typeString
            } else {
                val simpleTypeName = typeString.substringAfterLast('.')
                "${typeString}Supplier.${simpleTypeName}SupplierBuilder"
            }
        } else {
            // Fallback - assume it needs SupplierBuilder if we can't determine
            val simpleTypeName = typeString.substringAfterLast('.')
            "${typeString}Supplier.${simpleTypeName}SupplierBuilder"
        }
    }

    /**
     * Process generic types like Map<String, Integer> or Map<D4, Long>
     */
    private fun processGenericType(typeString: String, psiType: PsiType?, model: FieldModel): String {
        // Extract the raw type and generic parameters
        val rawType = typeString.substringBefore('<')
        val genericsContent = extractGenericsContent(typeString)

        if (genericsContent.isEmpty()) {
            return typeString
        }

        // Split generic parameters and process each one
        val genericParams = splitGenericParameters(genericsContent)
        val processedParams = mutableListOf<String>()

        // If we have PsiType information, use it to get parameter types
        if (psiType is PsiClassType && psiType.parameters.isNotEmpty()) {
            psiType.parameters.forEachIndexed { index, paramPsiType ->
                val paramTypeString = genericParams.getOrNull(index) ?: paramPsiType.canonicalText
                val paramHandler = TypeHandlerRegistry.resolve(
                    paramTypeString,
                    paramPsiType,
                    "FieldSection processGenericType param$index"
                )

                val processedParam = if (paramHandler.isKnown) {
                    paramTypeString
                } else {
                    // Recursively process if it's also a generic type
                    if (paramTypeString.contains('<')) {
                        processGenericType(paramTypeString, paramPsiType, model)
                    } else {
                        val simpleTypeName = paramTypeString.substringAfterLast('.')
                        "${paramTypeString}Supplier.${simpleTypeName}SupplierBuilder"
                    }
                }
                processedParams.add(processedParam)
            }
        } else {
            // Fallback - process without PsiType information
            genericParams.forEach { param ->
                val processedParam = processType(param.trim(), null, null, model)
                processedParams.add(processedParam)
            }
        }

        return "$rawType<${processedParams.joinToString(", ")}>"
    }

    /**
     * Extract content between < and > handling nested generics
     */
    private fun extractGenericsContent(typeString: String): String {
        val start = typeString.indexOf('<')
        val end = typeString.lastIndexOf('>')

        if (start == -1 || end == -1 || end <= start) {
            return ""
        }

        return typeString.substring(start + 1, end)
    }

    /**
     * Split generic parameters respecting nested generics
     * testdata: "String, Map<D4, Long>" -> ["String", "Map<D4, Long>"]
     */
    private fun splitGenericParameters(genericsContent: String): List<String> {
        val params = mutableListOf<String>()
        var currentParam = StringBuilder()
        var depth = 0

        for (char in genericsContent) {
            when (char) {
                '<' -> {
                    depth++
                    currentParam.append(char)
                }

                '>' -> {
                    depth--
                    currentParam.append(char)
                }

                ',' -> {
                    if (depth == 0) {
                        params.add(currentParam.toString().trim())
                        currentParam = StringBuilder()
                    } else {
                        currentParam.append(char)
                    }
                }

                else -> {
                    currentParam.append(char)
                }
            }
        }

        // Add the last parameter
        if (currentParam.isNotEmpty()) {
            params.add(currentParam.toString().trim())
        }

        return params
    }

    // Keep the old methods for backward compatibility if needed
    private fun renderKnownMapField(field: FieldModel): String {
        return renderMapField(field)
    }

    private fun renderUnknownMapField(field: FieldModel): String {
        return renderMapField(field)
    }
}