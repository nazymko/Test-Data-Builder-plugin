package com.testdata.suppliergen.model

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType

/**
 * Represents a field in a class with all necessary information for code generation.
 */
data class FieldModel(
    // Basic field information
    val name: String, // "userName"
    val typeHandler: TypeHandler, // TypeHandler for the field type, e.g., StringHandler, ListHandler
    val type: String, // "String", "List<User>", "Optional<Integer>"
    val fqType: String, // "java.lang.String", "java.util.Optional"
    val defaultValue: String, // "null", "new ArrayList<>()", "Optional.empty()"
    val randomizedValue: String, // "\"randomString123\"", "Optional.of(42)"
    val getter: String, // "getUserName", "getId"
    val setter: String, // "setUserName", "setId"
    val isKnown: Boolean, // true for String, false for custom classes
    val psiType: PsiType, // PSI representation of the field type
    val psiClass: PsiClass?, // PsiClass for "com.testdata.User"
    val extraImports: Set<String> = emptySet(), // ["java.time.LocalDateTime"]

    // Collection-specific properties (List<String>)
    val isCollection: Boolean = false, // true for List, Set, etc.
    val collectionRawType: String? = null, // "ArrayList", "HashSet"
    val elementPresentableType: String? = null, // "String", "User"
    val elementFqType: String? = null, // "java.lang.String"
    val elementIsKnown: Boolean = false, // true if element has handler

    // Optional-specific properties (Optional<String>)
    val isOptional: Boolean = false, // true if wrapped in Optional
    val optionalInnerType: String? = null, // "String", "List<User>"
    val optionalInnerFqType: String? = null, // "java.lang.String"
    val optionalInnerIsKnown: Boolean = false, // true if inner type has handler

    // Map-specific properties (Map<String, User>)
    val isMap: Boolean = false, // true for Map types
    val mapRawType: String? = null, // "HashMap", "TreeMap"
    val mapPsiType: PsiClass? = null, // "HashMap", "TreeMap"
    val keyType: String? = null, // "String", "Integer"
    val keyPsiType: PsiType? = null, // "String", "Integer"
    val valueType: String? = null, // "User", "List<String>"
    val valuePsiType: PsiType? = null, // "User", "List<String>"
    val keyIsKnown: Boolean = false, // true if key type has handler
    val valueIsKnown: Boolean = false, // true if value type has handler
)