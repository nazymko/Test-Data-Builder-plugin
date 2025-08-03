package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.impl.collections.helpers.BaseCollectionHandler
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil

// Concrete implementation for Guava Collections
object GuavaCollectionHandler : BaseCollectionHandler() {

    override val typeToFactoryMappings = mapOf(
        "com.google.common.collect.ListMultimap" to "com.google.common.collect.ArrayListMultimap",
        "ListMultimap" to "com.google.common.collect.ArrayListMultimap",
        "com.google.common.collect.SetMultimap" to "com.google.common.collect.HashMultimap",
        "SetMultimap" to "com.google.common.collect.HashMultimap",
        "com.google.common.collect.Multimap" to "com.google.common.collect.ArrayListMultimap",
        "Multimap" to "com.google.common.collect.ArrayListMultimap",
        "com.google.common.collect.ArrayListMultimap" to "com.google.common.collect.ArrayListMultimap",
        "ArrayListMultimap" to "com.google.common.collect.ArrayListMultimap",
        "com.google.common.collect.HashMultimap" to "com.google.common.collect.HashMultimap",
        "HashMultimap" to "com.google.common.collect.HashMultimap",
        "com.google.common.collect.TreeMultimap" to "com.google.common.collect.TreeMultimap",
        "TreeMultimap" to "com.google.common.collect.TreeMultimap",

        // Guava Collections
        "com.google.common.collect.ImmutableList" to "com.google.common.collect.ImmutableList",
        "ImmutableList" to "com.google.common.collect.ImmutableList",
        "com.google.common.collect.ImmutableSet" to "com.google.common.collect.ImmutableSet",
        "ImmutableSet" to "com.google.common.collect.ImmutableSet"
    )

    override fun hasSpecialHandling(factoryType: String): Boolean =
        factoryType.startsWith("com.google.common.collect.")

    override fun handleSpecialType(factoryType: String, psiType: PsiType, fieldName: String): String =
        when {
            factoryType.contains("ArrayListMultimap") -> "com.google.common.collect.ArrayListMultimap.create()"
            factoryType.contains("HashMultimap") -> "com.google.common.collect.HashMultimap.create()"
            factoryType.contains("TreeMultimap") -> "com.google.common.collect.TreeMultimap.create()"
            factoryType.contains("ImmutableList") -> handleImmutableList(psiType, fieldName)
            factoryType.contains("ImmutableSet") -> handleImmutableSet(psiType, fieldName)
            else -> "com.google.common.collect.ArrayListMultimap.create()"
        }

    private fun handleImmutableList(psiType: PsiType, fieldName: String): String {
        val generics = (psiType as? PsiClassType)?.parameters
        val elementType = generics?.firstOrNull()

        return if (elementType != null) {
            val elementValue = getElementValue(elementType, fieldName)
            "com.google.common.collect.ImmutableList.of($elementValue)"
        } else {
            "com.google.common.collect.ImmutableList.of()"
        }
    }

    private fun handleImmutableSet(psiType: PsiType, fieldName: String): String {
        val generics = (psiType as? PsiClassType)?.parameters
        val elementType = generics?.firstOrNull()

        return if (elementType != null) {
            val elementValue = getElementValue(elementType, fieldName)
            "com.google.common.collect.ImmutableSet.of($elementValue)"
        } else {
            "com.google.common.collect.ImmutableSet.of()"
        }
    }

    private fun getElementValue(elementType: PsiType, fieldName: String): String {
        val elementFq = PsiUtil.resolveClassInClassTypeOnly(elementType)?.qualifiedName
        val elementHandler = TypeHandlerRegistry.resolve(elementFq, elementType, "Guava element")

        return if (elementHandler.isKnown) {
            elementHandler.defaultValue(fieldName, elementFq, elementType)
        } else {
            "${elementType.presentableText}Supplier.configuredBuilder()"
        }
    }

    override fun getDefaultFallback(): String = "com.google.common.collect.ArrayListMultimap.create()"

    override fun getDefaultMapping(): Pair<String, String> =
        Pair("com.google.common.collect.Multimap", "com.google.common.collect.ArrayListMultimap")
}