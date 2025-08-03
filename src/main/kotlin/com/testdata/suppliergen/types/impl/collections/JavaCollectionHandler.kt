package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.helpers.BaseCollectionHandler
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil

// Concrete implementation for Java Collections
object JavaCollectionHandler : BaseCollectionHandler() {

    override val typeToFactoryMappings = mapOf(
        // Basic Collections
        "java.util.List" to "java.util.ArrayList",
        "List" to "java.util.ArrayList",
        "java.util.Set" to "java.util.HashSet",
        "Set" to "java.util.HashSet",
        "java.util.Collection" to "java.util.ArrayList",
        "Collection" to "java.util.ArrayList",
        "java.util.Queue" to "java.util.LinkedList",
        "Queue" to "java.util.LinkedList",
        "java.util.Deque" to "java.util.LinkedList",
        "Deque" to "java.util.LinkedList",
        "java.util.NavigableSet" to "java.util.TreeSet",
        "NavigableSet" to "java.util.TreeSet",
        "java.util.SortedSet" to "java.util.TreeSet",
        "SortedSet" to "java.util.TreeSet",
        "java.util.Map" to "java.util.HashMap",
        "Map" to "java.util.HashMap",

        // Concrete Collections
        "java.util.ArrayList" to "java.util.ArrayList",
        "ArrayList" to "java.util.ArrayList",
        "java.util.LinkedList" to "java.util.LinkedList",
        "LinkedList" to "java.util.LinkedList",
        "java.util.HashSet" to "java.util.HashSet",
        "HashSet" to "java.util.HashSet",
        "java.util.TreeSet" to "java.util.TreeSet",
        "TreeSet" to "java.util.TreeSet",
        "java.util.HashMap" to "java.util.HashMap",
        "HashMap" to "java.util.HashMap",
        "java.util.TreeMap" to "java.util.TreeMap",
        "TreeMap" to "java.util.TreeMap",
        "java.util.Vector" to "java.util.Vector",
        "Vector" to "java.util.Vector",
        "java.util.Stack" to "java.util.Stack",
        "Stack" to "java.util.Stack",
        "java.util.BitSet" to "java.util.BitSet",
        "BitSet" to "java.util.BitSet",
        "java.util.EnumSet" to "java.util.EnumSet",
        "EnumSet" to "java.util.EnumSet"
    )

    override fun hasSpecialHandling(factoryType: String): Boolean =
        factoryType == "java.util.BitSet" ||
                factoryType == "java.util.EnumSet"

    override fun handleSpecialType(factoryType: String, psiType: PsiType, fieldName: String): String =
        when (factoryType) {
            "java.util.BitSet" -> "new java.util.BitSet()"
            "java.util.EnumSet" -> handleEnumSet(psiType)
            else -> super.handleSpecialType(factoryType, psiType, fieldName)
        }

    private fun handleEnumSet(psiType: PsiType): String {
        val generics = (psiType as? PsiClassType)?.parameters
        val enumType = generics?.firstOrNull()
        val enumFq = enumType?.let { PsiUtil.resolveClassInClassTypeOnly(it)?.qualifiedName }

        return if (enumFq != null) {
            "java.util.EnumSet.noneOf($enumFq.class)"
        } else {
            "java.util.EnumSet.noneOf(java.time.DayOfWeek.class)" // fallback
        }
    }
}