package com.testdata.suppliergen.types.impl.collections.helpers

import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil

// Abstract base class for all collection handlers
abstract class BaseCollectionHandler : TypeHandler {

    // Abstract property that each subclass must define
    protected abstract val typeToFactoryMappings: Map<String, String>

    // Template method pattern - common logic for all collection handlers
    override val supportedTypes: Set<String> by lazy {
        val keys = typeToFactoryMappings.keys
        keys + keys.map { it.substringAfterLast('.') }
    }

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        if (fqName == null) return false

        val rawFqName = fqName.substringBefore('<')
        val simpleName = rawFqName.substringAfterLast('.')

        return supportedTypes.any { it == rawFqName || it == simpleName }
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        if (fqName == null || psiType == null) return getDefaultFallback()

        val rawFqName = fqName.substringBefore('<')
        val factoryEntry = findFactoryMapping(rawFqName)
        val (_, factoryType) = factoryEntry

        return when {
            hasSpecialHandling(factoryType) -> handleSpecialType(factoryType, psiType, fieldName)
            else -> handleGenericCollection(factoryType, psiType, fieldName)
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String>
        get() = supportedTypes.filter { it.contains('.') }.toSet()

    // Template methods that subclasses can override
    protected open fun getDefaultFallback(): String = "new java.util.ArrayList<>()"

    protected open fun hasSpecialHandling(factoryType: String): Boolean = false

    protected open fun handleSpecialType(factoryType: String, psiType: PsiType, fieldName: String): String =
        "new $factoryType<>()"

    // Common helper methods
    protected fun findFactoryMapping(rawFqName: String): Pair<String, String> {
        return typeToFactoryMappings.entries
            .map { it.toPair() }
            .firstOrNull {
                it.first == rawFqName || it.first.substringAfterLast('.') == rawFqName.substringAfterLast('.')
            } ?: getDefaultMapping()
    }

    protected open fun getDefaultMapping(): Pair<String, String> =
        Pair("java.util.List", "java.util.ArrayList")

    protected fun handleGenericCollection(factoryType: String, psiType: PsiType, fieldName: String): String {
        val generics = (psiType as? PsiClassType)?.parameters
        return when {
            generics == null || generics.isEmpty() -> "new $factoryType<>()"
            generics.size == 1 -> handleSingleGenericCollection(factoryType, generics[0], fieldName)
            generics.size == 2 -> handleDoubleGenericCollection(factoryType, generics[0], generics[1], fieldName)
            else -> "new $factoryType<>()"
        }
    }

    protected open fun handleSingleGenericCollection(
        factoryType: String,
        elementType: PsiType,
        fieldName: String
    ): String {
        val elementValue = getElementValue(elementType, fieldName)
        return when {
            isListLikeCollection(factoryType) -> "new $factoryType<>(java.util.Arrays.asList($elementValue))"
            isSetLikeCollection(factoryType) -> "new $factoryType<>(java.util.Arrays.asList($elementValue))"
            isQueueLikeCollection(factoryType) -> "new $factoryType<>(java.util.Arrays.asList($elementValue))"
            else -> "new $factoryType<>()"
        }
    }

    protected open fun handleDoubleGenericCollection(
        factoryType: String,
        keyType: PsiType,
        valueType: PsiType,
        fieldName: String
    ): String {
        val keyValue = getElementValue(keyType, fieldName + "Key")
        val valueValue = getElementValue(valueType, fieldName + "Value")

        return when {
            isMapLikeCollection(factoryType) -> "new $factoryType<>(java.util.Map.of($keyValue, $valueValue))"
            else -> "new $factoryType<>()"
        }
    }

    // Helper methods for collection type classification
    protected fun isListLikeCollection(factoryType: String): Boolean =
        factoryType.contains("ArrayList") || factoryType.contains("LinkedList") ||
                factoryType.contains("Vector") || factoryType.contains("Stack") || factoryType.contains("ArrayDeque")

    protected fun isSetLikeCollection(factoryType: String): Boolean =
        factoryType.contains("HashSet") || factoryType.contains("TreeSet") || factoryType.contains("SkipListSet")

    protected fun isQueueLikeCollection(factoryType: String): Boolean =
        factoryType.contains("Queue") || factoryType.contains("Deque")

    protected fun isMapLikeCollection(factoryType: String): Boolean =
        factoryType.contains("Map") && !factoryType.contains("Multimap")

    private fun getElementValue(elementType: PsiType, fieldName: String): String {
        val elementFq = PsiUtil.resolveClassInClassTypeOnly(elementType)?.qualifiedName
        val elementHandler = TypeHandlerRegistry.resolve(elementFq, elementType, "Collection element")

        return if (elementHandler.isKnown) {
            elementHandler.defaultValue(fieldName, elementFq, elementType)
        } else {
            "${elementType.presentableText}Supplier.configuredBuilder()"
        }
    }
}