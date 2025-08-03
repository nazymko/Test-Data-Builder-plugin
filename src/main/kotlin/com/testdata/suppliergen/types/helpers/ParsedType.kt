package com.testdata.suppliergen.types.helpers

data class ParsedType(
    val rawType: String,
    val generics: List<String>,
    val isParsable: Boolean,
    val error: String? = null
) {
    val isGeneric: Boolean get() = generics.isNotEmpty()
    val firstGeneric: String? get() = generics.firstOrNull()
    val secondGeneric: String? get() = generics.getOrNull(1)
}

object TypeParser {
    
    fun parseType(typeString: String): ParsedType {
        if (typeString.isBlank()) {
            return ParsedType("", emptyList(), false, "Empty type string")
        }
        
        val trimmed = typeString.trim()
        
        // Check if it has generics
        val openBracket = trimmed.indexOf('<')
        val closeBracket = trimmed.lastIndexOf('>')
        
        // No generics case
        if (openBracket == -1 && closeBracket == -1) {
            return if (isValidJavaType(trimmed)) {
                ParsedType(trimmed, emptyList(), true)
            } else {
                ParsedType(trimmed, emptyList(), false, "Invalid Java type format")
            }
        }
        
        // Invalid bracket structure
        if (openBracket == -1 || closeBracket == -1 || closeBracket <= openBracket) {
            return ParsedType(trimmed, emptyList(), false, "Invalid generic bracket structure")
        }
        
        // Extract raw type and generics
        val rawType = trimmed.substring(0, openBracket).trim()
        val genericsString = trimmed.substring(openBracket + 1, closeBracket).trim()
        
        // Validate raw type
        if (!isValidJavaType(rawType)) {
            return ParsedType(rawType, emptyList(), false, "Invalid raw type format")
        }
        
        // Parse generics with proper bracket balancing
        val generics = try {
            parseGenerics(genericsString)
        } catch (e: Exception) {
            return ParsedType(rawType, emptyList(), false, "Failed to parse generics: ${e.message}")
        }
        
        // Validate all generic types
        val invalidGeneric = generics.find { !isValidJavaType(it) }
        if (invalidGeneric != null) {
            return ParsedType(rawType, generics, false, "Invalid generic type: $invalidGeneric")
        }
        
        return ParsedType(rawType, generics, true)
    }
    
    private fun parseGenerics(genericsString: String): List<String> {
        if (genericsString.isEmpty()) return emptyList()
        
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var bracketDepth = 0
        
        for (char in genericsString) {
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
                        // Top-level comma, split here
                        val type = current.toString().trim()
                        if (type.isNotEmpty()) {
                            result.add(type)
                        }
                        current.clear()
                    } else {
                        // Nested comma, keep as part of current type
                        current.append(char)
                    }
                }
                else -> {
                    current.append(char)
                }
            }
        }
        
        // Add the last part
        val lastType = current.toString().trim()
        if (lastType.isNotEmpty()) {
            result.add(lastType)
        }
        
        return result
    }
    
    private fun isValidJavaType(type: String): Boolean {
        if (type.isBlank()) return false
        
        // Basic validation for Java type format
        // Should start with letter or package separator
        // Can contain letters, digits, dots, underscores, dollar signs
        val javaTypeRegex = Regex("^[a-zA-Z_$][a-zA-Z0-9_.$]*$")
        
        return javaTypeRegex.matches(type) && 
               !type.startsWith(".") && 
               !type.endsWith(".") &&
               !type.contains("..")
    }
}