package com.testdata.suppliergen.types.impl.maps

import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil

// Guava Map Handler (BiMap and other special map types)
object GuavaMapHandler : CommonMapHandler() {
    
    override val supportedTypes: Set<String> = setOf(
        "com.google.common.collect.BiMap",
        "BiMap",
        "com.google.common.collect.HashBiMap",
        "HashBiMap",
        "com.google.common.collect.ImmutableMap",
        "ImmutableMap",
        "com.google.common.collect.ImmutableBiMap",
        "ImmutableBiMap"
    )

    override fun returnMap(): String = "com.google.common.collect.BiMap"
    override fun constructorMap(): String = "com.google.common.collect.HashBiMap"

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val rawType = fqName?.substringBefore('<') ?: ""

        return when {
            rawType.contains("HashBiMap") -> "com.google.common.collect.HashBiMap.create()"
            rawType.contains("ImmutableMap") -> handleImmutableMap(psiType, fieldName)
            rawType.contains("ImmutableBiMap") -> handleImmutableBiMap(psiType, fieldName)
            rawType.contains("BiMap") -> "com.google.common.collect.HashBiMap.create()"
            else -> super.defaultValue(fieldName, fqName, psiType)
        }
    }

    private fun handleImmutableMap(psiType: PsiType?, fieldName: String): String {
        val generics = (psiType as? PsiClassType)?.parameters
        return if (generics != null && generics.size >= 2) {
            val keyValue = getElementValue(generics[0], fieldName + "Key")
            val valueValue = getElementValue(generics[1], fieldName + "Value")
            "com.google.common.collect.ImmutableMap.of($keyValue, $valueValue)"
        } else {
            "com.google.common.collect.ImmutableMap.of()"
        }
    }

    private fun handleImmutableBiMap(psiType: PsiType?, fieldName: String): String {
        val generics = (psiType as? PsiClassType)?.parameters
        return if (generics != null && generics.size >= 2) {
            val keyValue = getElementValue(generics[0], fieldName + "Key")
            val valueValue = getElementValue(generics[1], fieldName + "Value")
            "com.google.common.collect.ImmutableBiMap.of($keyValue, $valueValue)"
        } else {
            "com.google.common.collect.ImmutableBiMap.of()"
        }
    }

    private fun getElementValue(elementType: PsiType, fieldName: String): String {
        val elementFq = PsiUtil.resolveClassInClassTypeOnly(elementType)?.qualifiedName
        val elementHandler = TypeHandlerRegistry.resolve(elementFq, elementType, "GuavaMap element")

        return if (elementHandler.isKnown) {
            elementHandler.defaultValue(fieldName, elementFq, elementType)
        } else {
            "${elementType.presentableText}Supplier.configuredBuilder()"
        }
    }
}