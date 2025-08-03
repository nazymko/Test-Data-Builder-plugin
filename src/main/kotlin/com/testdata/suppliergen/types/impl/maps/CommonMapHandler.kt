package com.testdata.suppliergen.types.impl.maps

import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.contract.HelperMethodMetadata
import com.testdata.suppliergen.types.contract.TypeHandler
import com.testdata.suppliergen.types.helpers.GenericSupportHelper
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType

open class CommonMapHandler : TypeHandler {

    override val supportedTypes: Set<String>
        get() = setOf("java.util.Map", "Map")

    open fun returnMap() = "java.util.Map"
    open fun constructorMap() = "java.util.HashMap"
    open fun classCreator(): String? = null;
    override val staticExtraImports: Set<String> = setOf(returnMap(), constructorMap())

    // Track which types are currently being processed to prevent recursion
    private val processingTypes = ThreadLocal.withInitial { mutableSetOf<String>() }

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        // Normalize fqName and psiType to raw types (strip generics)
        val rawFqName = fqName?.substringBefore('<')
        val rawPsiTypeText = (psiType as? PsiClassType)?.canonicalText?.substringBefore('<')
        // Check against supportedTypes using helper (matching full or simple names)
        val matchesFqName = rawFqName?.let { GenericSupportHelper.matchesAny(it, null, supportedTypes) } ?: false
        val matchesPsiType =
            rawPsiTypeText?.let { GenericSupportHelper.matchesAny(null, psiType, supportedTypes) } ?: false

        return matchesFqName || matchesPsiType
    }

    override fun customImports(fqName: String, psiType: PsiType): Set<String> {
        val (mapType, keyType, valueType) = extractTypes(fqName)
        val (key, value, map) = extractMapTypes(psiType)
        val keyHandler =
            TypeHandlerRegistry.resolve(keyType, key, "CommonMapHandler customImports KeyHandler")
        val valueHandler =
            TypeHandlerRegistry.resolve(valueType, value, "CommonMapHandler customImports ValueHandler")
        return keyHandler.staticExtraImports + valueHandler.staticExtraImports;
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val (mapType, keyType, valueType) = extractTypes(fqName ?: "Unknown")
        return "${prepareMethodName(mapType, keyType, valueType)}()"
    }

    override fun helperMethod(model: FieldModel, ctx: GenerationContext): HelperMethodMetadata? {
        return generatePreparedMethodBody(model, returnMap(), constructorClass = constructorMap(), ctx)
    }

    fun generatePreparedMethodBody(
        model: FieldModel,
        returnClass: String,
        constructorClass: String,
        ctx: GenerationContext
    ): HelperMethodMetadata {
        val (mapType, keyType, valueType) = extractTypes(model.fqType)
        val (key, value, map) = extractMapTypes(model.psiType)

        val keyHandler =
            TypeHandlerRegistry.resolve(keyType, key, "CommonMapHandler generatePreparedMethodBody KeyHandler")
        val valueHandler =
            TypeHandlerRegistry.resolve(valueType, value, "CommonMapHandler generatePreparedMethodBody ValueHandler")

        val entryLines = (1..3).joinToString("\n") { index ->
            val keyValue = keyHandler.randomizedValue("${model.name}_key_$index", keyType, null)
            val valueValue = valueHandler.randomizedValue("${model.name}_val_$index", valueType, null)
            "    supportMethodResult.put($keyValue, $valueValue);"
        }

        val methodName = prepareMethodName(mapType, keyType, valueType)

        var keyBuilderType = keyType
        val keyTypeSimpleName = (keyType ?: "Object").substringAfterLast('.')
        if (!keyHandler.isKnown) {
            keyBuilderType = keyType + "Supplier." + keyTypeSimpleName + "SupplierBuilder"
        }

        var valueBuilderType = valueType
        val valueTypeSimpleName = (valueType ?: "Object").substringAfterLast('.')
        if (!valueHandler.isKnown) {
            valueBuilderType = valueType + "Supplier." + valueTypeSimpleName + "SupplierBuilder"
        }
        val classCreatorVal = classCreator();
        var body = ""
        val creatorConstructor = """new ${constructorClass}<>(${
            optionalMapArgs(
                mapType,
                keyType,
                valueType
            )
        })"""
        var creatorLine = "";

        if (classCreatorVal != null && classCreatorVal.isNotBlank()) {
            creatorLine = classCreatorVal;
        } else {
            creatorLine = creatorConstructor;
        }

        if (keyType == null && valueType == null) {
            body = """
                private static $returnClass $methodName() {
                    $returnClass supportMethodResult = $creatorLine;
        $entryLines
                    return supportMethodResult;
                }
            """.trimIndent()
        } else {
            body = """
                private static $returnClass<$keyBuilderType, $valueBuilderType> $methodName() {
                
                    $returnClass<$keyBuilderType, $valueBuilderType> supportMethodResult = $creatorLine;
        $entryLines
                    return supportMethodResult;
                }
            """.trimIndent()
        }


        // Fixed: Prevent recursive helper method calls
        val dependencies = collectHelperMethodDependencies(keyHandler, valueHandler, model, ctx)

        return HelperMethodMetadata(body, methodName, dependencies)
    }

    /**
     * Safely collect helper method dependencies without causing recursion
     */
    private fun collectHelperMethodDependencies(
        keyHandler: TypeHandler,
        valueHandler: TypeHandler,
        model: FieldModel,
        ctx: GenerationContext
    ): List<HelperMethodMetadata> {
        val dependencies = mutableListOf<HelperMethodMetadata>()
        val processing = processingTypes.get()

        // Create unique keys for tracking recursion based on field type and handler role
        val keyTypeKey = "${model.fqType}_key_${keyHandler.javaClass.simpleName}"
        val valueTypeKey = "${model.fqType}_value_${valueHandler.javaClass.simpleName}"

        // Only process key handler if not already processing this type
        if (keyTypeKey !in processing) {
            processing.add(keyTypeKey)
            try {
                keyHandler.helperMethod(model, ctx)?.let { dependencies.add(it) }
            } catch (e: Exception) {
                println("Error processing key handler for ${model.fqType}: ${e.message}")
            } finally {
                processing.remove(keyTypeKey)
            }
        } else {
            println("Skipping recursive key handler for ${model.fqType} (${keyHandler.javaClass.simpleName})")
        }

        // Only process value handler if not already processing this type
        if (valueTypeKey !in processing) {
            processing.add(valueTypeKey)
            try {
                valueHandler.helperMethod(model, ctx)?.let { dependencies.add(it) }
            } catch (e: Exception) {
                println("Error processing value handler for ${model.fqType}: ${e.message}")
            } finally {
                processing.remove(valueTypeKey)
            }
        } else {
            println("Skipping recursive value handler for ${model.fqType} (${valueHandler.javaClass.simpleName})")
        }

        return dependencies
    }

    fun prepareMethodName(mapType: String?, keyType: String?, valueType: String?): String {
        fun sanitizeType(type: String): String =
            type.replace('.', '_')
                .replace("<", "_")
                .replace(">", "")
                .replace(", ", "_")
                .replace(",", "_")

        return "createPredefinedMap_${sanitizeType(mapType ?: "UnknownMapType")}_${sanitizeType(keyType ?: "UnknownKey")}_${
            sanitizeType(
                valueType ?: "UnknownValue"
            )
        }"
    }

    fun extractTypes(fqType: String): Triple<String?, String?, String?> {
        val start = fqType.indexOf('<')
        val end = fqType.lastIndexOf('>')

        if (start == -1 || end == -1 || end <= start) {
            return Triple(fqType.takeIf { it.isNotBlank() }, null, null)
        }

        val mapType = fqType.substring(0, start).trim()
        val generics = fqType.substring(start + 1, end).trim()

        // Split generics while respecting nested angle brackets
        val parts = splitRespectingBrackets(generics)

        val keyType = parts.getOrNull(0)?.trim()
        val valueType = parts.getOrNull(1)?.trim()

        return Triple(mapType, keyType, valueType)
    }

    private fun splitRespectingBrackets(input: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var bracketDepth = 0

        for (char in input) {
            when (char) {
                '<' -> {
                    bracketDepth++
                    current.append(char)
                }

                '>' -> {
                    bracketDepth--
                    current.append(char)
                }

                ',' -> {
                    if (bracketDepth == 0) {
                        // We're at the top level, so this comma separates type parameters
                        result.add(current.toString())
                        current.clear()
                    } else {
                        // We're inside nested generics, so this comma is part of the nested type
                        current.append(char)
                    }
                }

                else -> {
                    current.append(char)
                }
            }
        }

        // Add the last part
        if (current.isNotEmpty()) {
            result.add(current.toString())
        }

        return result
    }

    open fun optionalMapArgs(mapType: String?, keyType: String?, valueType: String?): String = ""

    fun extractMapTypes(mapType: PsiType): Triple<PsiType?, PsiType?, PsiClass?> {
        if (mapType !is PsiClassType) return Triple(null, null, null)

        val resolvedClass = mapType.resolve() ?: return Triple(null, null, null)
        val parameters = mapType.parameters

        val keyType = parameters.getOrNull(0)
        val valueType = parameters.getOrNull(1)

        return Triple(keyType, valueType, resolvedClass)
    }

    /**
     * Clear the processing stack for the current thread - useful for cleanup in tests
     */
    fun clearProcessingStack() {
        processingTypes.get().clear()
    }
}