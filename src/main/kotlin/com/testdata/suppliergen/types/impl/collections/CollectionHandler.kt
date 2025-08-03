package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.helpers.ParsedType
import com.testdata.suppliergen.types.helpers.TypeParser
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil

// TODO: remove maps from a collections handler
object CollectionHandler : CommonCollectionHandler() {

    private val collectionToFactoryMap = mapOf(
        // Basic Collections
        "java.util.List" to "java.util.ArrayList",               // new ArrayList<>()
        "List" to "java.util.ArrayList",                         // new ArrayList<>()
        "java.util.Set" to "java.util.HashSet",                 // new HashSet<>()
        "Set" to "java.util.HashSet",                           // new HashSet<>()
        "java.util.Collection" to "java.util.ArrayList",        // fallback to ArrayList
        "Collection" to "java.util.ArrayList",                  // fallback to ArrayList
        "java.util.Queue" to "java.util.LinkedList",            // new LinkedList<>()
        "Queue" to "java.util.LinkedList",                      // new LinkedList<>()
        "java.util.Deque" to "java.util.LinkedList",            // new LinkedList<>()
        "Deque" to "java.util.LinkedList",                      // new LinkedList<>()
        "java.util.NavigableSet" to "java.util.TreeSet",        // new TreeSet<>()
        "NavigableSet" to "java.util.TreeSet",                  // new TreeSet<>()
        "java.util.SortedSet" to "java.util.TreeSet",           // new TreeSet<>()
        "SortedSet" to "java.util.TreeSet",                     // new TreeSet<>()

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
        "java.util.Hashtable" to "java.util.Hashtable",
        "Hashtable" to "java.util.Hashtable",
        "java.util.Properties" to "java.util.Properties",
        "Properties" to "java.util.Properties",
        "java.util.PriorityQueue" to "java.util.PriorityQueue",
        "PriorityQueue" to "java.util.PriorityQueue",
        "java.util.ArrayDeque" to "java.util.ArrayDeque",
        "ArrayDeque" to "java.util.ArrayDeque",
        "java.util.BitSet" to "java.util.BitSet",
        "BitSet" to "java.util.BitSet",
        "java.util.EnumSet" to "java.util.EnumSet",
        "EnumSet" to "java.util.EnumSet",
        "java.util.LinkedHashSet" to "java.util.LinkedHashSet",
        "LinkedHashSet" to "java.util.LinkedHashSet",

        // Concurrent Collections
        "java.util.concurrent.BlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "BlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "java.util.concurrent.LinkedBlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "LinkedBlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "java.util.concurrent.ArrayBlockingQueue" to "java.util.concurrent.ArrayBlockingQueue",
        "ArrayBlockingQueue" to "java.util.concurrent.ArrayBlockingQueue",
        "java.util.concurrent.PriorityBlockingQueue" to "java.util.concurrent.PriorityBlockingQueue",
        "PriorityBlockingQueue" to "java.util.concurrent.PriorityBlockingQueue",
        "java.util.concurrent.LinkedBlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "LinkedBlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "java.util.concurrent.BlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "BlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "java.util.concurrent.ConcurrentHashMap" to "java.util.concurrent.ConcurrentHashMap",
        "ConcurrentHashMap" to "java.util.concurrent.ConcurrentHashMap",
        "java.util.concurrent.ConcurrentLinkedQueue" to "java.util.concurrent.ConcurrentLinkedQueue",
        "ConcurrentLinkedQueue" to "java.util.concurrent.ConcurrentLinkedQueue",
        "java.util.concurrent.ConcurrentLinkedDeque" to "java.util.concurrent.ConcurrentLinkedDeque",
        "ConcurrentLinkedDeque" to "java.util.concurrent.ConcurrentLinkedDeque",
        "java.util.concurrent.ConcurrentSkipListSet" to "java.util.concurrent.ConcurrentSkipListSet",
        "ConcurrentSkipListSet" to "java.util.concurrent.ConcurrentSkipListSet",
        "java.util.concurrent.ConcurrentSkipListMap" to "java.util.concurrent.ConcurrentSkipListMap",
        "ConcurrentSkipListMap" to "java.util.concurrent.ConcurrentSkipListMap",

        // Guava Collections
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
        "com.google.common.collect.BiMap" to "com.google.common.collect.HashBiMap",
        "BiMap" to "com.google.common.collect.HashBiMap",
        "com.google.common.collect.HashBiMap" to "com.google.common.collect.HashBiMap",
        "HashBiMap" to "com.google.common.collect.HashBiMap"
    )

    // More tolerant support check: ignores package prefixes, matches simple names or fully qualified
    override val supportedTypes: Set<String> by lazy {
        val keys = collectionToFactoryMap.keys
        keys + keys.map { it.substringAfterLast('.') } // e.g. "java.util.List" + "List"
    }

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        if (fqName == null) return false

        // Extract raw type FQCN from fqName (strip generics)
        val rawFqName = fqName.substringBefore('<')

        // Also get simple name without package
        val simpleName = rawFqName.substringAfterLast('.')

        // Check if rawFqName or simpleName is in supportedTypes
        return supportedTypes.any { it == rawFqName || it == simpleName }
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        if (fqName == null) return "new java.util.ArrayList<>()"

        val rawFqName = fqName.substringBefore('<')

        val (_, factoryType) = collectionToFactoryMap.entries
            .map { it.toPair() }
            .firstOrNull { it.first == rawFqName || it.first.substringAfterLast('.') == rawFqName.substringAfterLast('.') }
            ?: ("java.util.List" to "java.util.ArrayList")


        return when {
            factoryType == "java.util.BitSet" -> "new java.util.BitSet()"
            factoryType == "java.util.Properties" -> "new java.util.Properties()"
            factoryType.startsWith("java.util.concurrent.ArrayBlockingQueue") -> "new java.util.concurrent.ArrayBlockingQueue<>(16)"
            factoryType.startsWith("com.google.common.collect.") -> handleGuavaCollection(factoryType)
            factoryType == "java.util.EnumSet" -> handleEnumSet(psiType)
            else -> handleGenericCollection(factoryType, psiType, fieldName, fqName)
        }
    }

    private fun handleGenericCollection(
        factoryType: String,
        psiType: PsiType?,
        fieldName: String,
        fqName: String
    ): String {
        val parseType = TypeParser.parseType(fqName)
        if (parseType.isParsable) {
            val rawType = parseType.rawType
            val generics = parseType.generics
            return when (generics.size) {
                0 -> "new $factoryType<>()"
                1 -> handleSingleGenericCollection(factoryType, null, fieldName, parseType)
                2 -> handleDoubleGenericCollection(factoryType, null, null, fieldName, parseType)
                else -> "new $factoryType<>()"
            }
        } else {
            val generics = (psiType as? PsiClassType)?.parameters ?: return "new $factoryType<>()"
            return when (generics.size) {
                0 -> "new $factoryType<>()"
                1 -> handleSingleGenericCollection(factoryType, generics[0], fieldName, parseType)
                2 -> handleDoubleGenericCollection(factoryType, generics[0], generics[1], fieldName, parseType)
                else -> "new $factoryType<>()"
            }
        }
    }

    private fun handleSingleGenericCollection(
        factoryType: String,
        elementType: PsiType?,
        fieldName: String,
        parseType: ParsedType
    ): String {

        val elementFq = PsiUtil.resolveClassInClassTypeOnly(elementType)?.qualifiedName ?: parseType.firstGeneric
        val elementHandler = TypeHandlerRegistry.resolve(elementFq, elementType, "CollectionHandler defaultValue")

        // Generate 1-3 sample values based on whether element type is known
        val sampleValues = if (elementHandler.isKnown) {
            (1..2).map { index ->
                elementHandler.randomizedValue("${fieldName}_elem_$index", elementFq, elementType)
            }
        } else {
            listOf("${elementType?.presentableText ?: parseType.rawType}Supplier.configuredBuilder()")
        }

        return when {
            // Special collection types with unique initialization
            factoryType == "java.util.Stack" ->
                "new $factoryType<>() {{ ${sampleValues.joinToString("; ") { "push($it)" }}; }}"

            factoryType == "java.util.EnumSet" ->
                handleEnumSetForSingle(elementType, parseType.firstGeneric)

            // Collections that work well with Arrays.asList()
            factoryType in listOf(
                "java.util.ArrayList", "java.util.LinkedList", "java.util.Vector",
                "java.util.HashSet", "java.util.TreeSet", "java.util.LinkedHashSet",
                "java.util.PriorityQueue", "java.util.ArrayDeque"
            ) -> "new $factoryType<>(java.util.Arrays.asList(${sampleValues.joinToString(", ")}))"

            // Concurrent collections that support Arrays.asList() constructor
            factoryType in listOf(
                "java.util.concurrent.LinkedBlockingQueue",
                "java.util.concurrent.PriorityBlockingQueue",
                "java.util.concurrent.LinkedBlockingDeque",
                "java.util.concurrent.ConcurrentLinkedQueue",
                "java.util.concurrent.ConcurrentLinkedDeque",
                "java.util.concurrent.ConcurrentSkipListSet"
            ) -> "new $factoryType<>(java.util.Arrays.asList(${sampleValues.joinToString(", ")}))"

            // Default: use add() method initialization
            else -> "new $factoryType<>() {{ ${sampleValues.joinToString("; ") { "add($it)" }}; }}"
        }
    }

    override fun randomizedValue(
        fieldName: String,
        fqName: String?,
        psiType: PsiType?
    ): String {
        return defaultValue(fieldName, fqName, psiType)
    }

    private fun handleDoubleGenericCollection(
        factoryType: String,
        keyType: PsiType?,
        valueType: PsiType?,
        fieldName: String,
        parseType: ParsedType
    ): String {
        val keyFq = PsiUtil.resolveClassInClassTypeOnly(keyType)?.qualifiedName ?: parseType.firstGeneric
        val valueFq = PsiUtil.resolveClassInClassTypeOnly(valueType)?.qualifiedName ?: parseType.secondGeneric

        val keyHandler = TypeHandlerRegistry.resolve(keyFq, keyType, "CollectionHandler key")
        val valueHandler = TypeHandlerRegistry.resolve(valueFq, valueType, "CollectionHandler value")

        val keyValue = if (keyHandler.isKnown) {
            keyHandler.randomizedValue("${fieldName}_key", keyFq, keyType)
        } else {
            "${keyType?.presentableText ?: parseType.firstGeneric}Supplier.configuredBuilder()"
        }

        val valueValue = if (valueHandler.isKnown) {
            valueHandler.randomizedValue("${fieldName}_val", valueFq, valueType)
        } else {
            "${valueType?.presentableText?:parseType.secondGeneric}Supplier.configuredBuilder()"
        }

        return when {
            // Map types - use Map.of() for single entry initialization
            factoryType in listOf(
                "java.util.HashMap", "java.util.TreeMap", "java.util.LinkedHashMap",
                "java.util.Hashtable", "java.util.concurrent.ConcurrentHashMap",
                "java.util.concurrent.ConcurrentSkipListMap"
            ) -> "new $factoryType<>(java.util.Map.of($keyValue, $valueValue))"

            // Collections holding Map.Entry or similar pairs
            else -> "new $factoryType<>(java.util.Arrays.asList(new java.util.AbstractMap.SimpleEntry<>($keyValue, $valueValue)))"
        }
    }

    private fun handleEnumSetForSingle(elementType: PsiType?, fqName: String?): String {
        val enumFq = PsiUtil.resolveClassInClassTypeOnly(elementType)?.qualifiedName ?: fqName
        return if (enumFq != null) {
            "java.util.EnumSet.noneOf($enumFq.class)"
        } else {
            "java.util.EnumSet.noneOf(java.time.DayOfWeek.class)" // fallback
        }
    }

    private fun handleEnumSet(psiType: PsiType?): String {
        val generics = (psiType as? PsiClassType)?.parameters
        val enumType = generics?.firstOrNull()
        val enumFq = enumType?.let { PsiUtil.resolveClassInClassTypeOnly(it)?.qualifiedName }

        return if (enumFq != null) {
            "java.util.EnumSet.noneOf($enumFq.class)"
        } else {
            "java.util.EnumSet.noneOf(java.time.DayOfWeek.class)" // fallback
        }
    }

    private fun handleGuavaCollection(factoryType: String): String {
        return when {
            factoryType.contains("ArrayListMultimap") -> "com.google.common.collect.ArrayListMultimap.create()"
            factoryType.contains("HashMultimap") -> "com.google.common.collect.HashMultimap.create()"
            factoryType.contains("TreeMultimap") -> "com.google.common.collect.TreeMultimap.create()"
            factoryType.contains("HashBiMap") -> "com.google.common.collect.HashBiMap.create()"
            else -> "com.google.common.collect.ArrayListMultimap.create()"
        }

    }

    fun inspect(psiType: PsiType): ElementInfo {
        val rawTypeFq = (psiType as? PsiClassType)?.resolve()?.qualifiedName ?: "java.util.ArrayList"
        val paramType = PsiUtil.extractIterableTypeParameter(psiType, false)
        val elementPresentable = paramType?.presentableText
        val elementFq = PsiUtil.resolveClassInClassTypeOnly(paramType ?: psiType)?.qualifiedName
        val elementIsKnown = elementFq != null && TypeHandlerRegistry.resolve(
            elementFq,
            paramType ?: psiType,
            "CollectionHandler inspect"
        ).isKnown

        return ElementInfo(
            rawType = rawTypeFq,
            elementPresentableType = elementPresentable,
            elementFqType = elementFq,
            elementIsKnown = elementIsKnown
        )
    }

    data class ElementInfo(
        val rawType: String,
        val elementPresentableType: String?,
        val elementFqType: String?,
        val elementIsKnown: Boolean
    )
}