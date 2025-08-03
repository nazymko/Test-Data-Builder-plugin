package com.testdata.suppliergen.generator.sections

import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.model.InstantiationMode
import com.testdata.suppliergen.model.SupplierClassModel

class GetMethodSection : SectionBuilder {
    override fun render(model: SupplierClassModel): String = when (model.instantiationMode) {
        InstantiationMode.SETTERS -> renderWithSetters(model)
        InstantiationMode.BUILDER -> renderWithBuilder(model)
        InstantiationMode.CONSTRUCTOR -> renderWithConstructor(model)
    }

    // --- Mode 1: SETTERS -----------------------------------------------------
    private fun renderWithSetters(model: SupplierClassModel): String {
        val t = model.targetClassName
        val body = buildString {
            appendLine("    @Override")
            appendLine("    public $t get() {")
            appendLine("        $t result = new $t();")
            model.fields.forEach { f ->
                val cap = f.name.replaceFirstChar { it.uppercaseChar() }
                val expr = valueExprForField(f)
                appendLine("        result.set$cap($expr);")
            }
            appendLine("        return result;")
            appendLine("    }")
        }
        return body
    }

    // --- Mode 2: BUILDER -----------------------------------------------------
    private fun renderWithBuilder(model: SupplierClassModel): String {
        val t = model.targetClassName
        val b = StringBuilder()
        b.appendLine("    @Override")
        b.appendLine("    public $t get() {")
        b.appendLine("        return $t.builder()")

        model.fields.forEach { f ->
            val expr = valueExprForField(f)
            b.appendLine("            .${f.name}($expr)")
        }

        b.appendLine("            .build();")
        b.appendLine("    }")
        return b.toString()
    }

    // --- Mode 3: CONSTRUCTOR -------------------------------------------------
    private fun renderWithConstructor(model: SupplierClassModel): String {
        val t = model.targetClassName

        // Choose order: either provided or fields order
        val ordered = if (model.ctorParamOrder.isNotEmpty()) model.ctorParamOrder else model.fields

        // Join arguments each on its own line with indentation
        val args = ordered.joinToString(",\n                    ") { valueExprForField(it) }

        return """
        @Override
        public $t get() {
            return new $t(
                    $args
            );
        }
    """.trimIndent()
    }

    // Helper to generate the correct value expression for a field
    private fun valueExprForField(f: FieldModel): String {
        return when {
            f.isMap -> {
                // Handle maps based on key/value known status
                handleMapField(f)
            }

            f.isCollection && f.elementIsKnown -> {
                f.name
            }

            f.isCollection && !f.elementIsKnown -> {
               if (f.collectionRawType!== null &&
                   f.collectionRawType.contains("java.util.Set")&&
                   f.collectionRawType.contains("Set")) {
                   // Elements are not known, map to build()
                   "${f.name} != null ? ${f.name}.stream().map(d -> d.build().get())" +
                           ".collect(java.util.stream.Collectors.toSet()) : null"
                } else {
                   // Elements are not known, map to build()
                   "${f.name} != null ? ${f.name}.stream().map(d -> d.build().get())" +
                           ".collect(java.util.stream.Collectors.toList()) : null"
                }
            }

            !f.isKnown -> {
                // Field is not known, call build()
                "${f.name} != null ? ${f.name}.build().get() : null"
            }

            else -> f.name
        }
    }

    // Helper to handle map field transformations
    private fun handleMapField(f: FieldModel): String {
        val keyIsKnown = f.keyIsKnown
        val valueIsKnown = f.valueIsKnown

        return when {
            keyIsKnown && valueIsKnown -> {
                // Both key and value are known types, use map as-is
                f.name
            }

            !keyIsKnown && !valueIsKnown -> {
                if (f.keyPsiType == null && f.valuePsiType == null) {
                    f.name
                } else {
                    // Both key and value are not known, transform both
                    "${f.name} != null ? ${f.name}.entrySet().stream()" +
                            ".collect(${getMapCollectorWithMerge(f, getKeyTransformation(f), getValueTransformation(f))}) : null"
                }
            }

            !keyIsKnown && valueIsKnown -> {
                // Key is not known, value is known - only transform key
                "${f.name} != null ? ${f.name}.entrySet().stream()" +
                        ".collect(${getMapCollectorWithMerge(f, getKeyTransformation(f), "entry.getValue()")}) : null"
            }

            keyIsKnown && !valueIsKnown -> {
                // Key is known, value is not known - only transform value
                "${f.name} != null ? ${f.name}.entrySet().stream()" +
                        ".collect(${getMapCollectorWithMerge(f, "entry.getKey()", getValueTransformation(f))}) : null"
            }

            else -> f.name
        }
    }

    // Helper to get the correct key transformation
    private fun getKeyTransformation(f: FieldModel): String {
        return if (f.keyPsiType?.toString()?.contains("Builder") == true) {
            // If it's a Builder type, just use .build()
            "entry.getKey().build()"
        } else {
            // If it's not a Builder type but needs transformation, use .build().get()
            "entry.getKey().build().get()"
        }
    }

    // Helper to get the correct value transformation
    private fun getValueTransformation(f: FieldModel): String {
        return if (f.valuePsiType?.toString()?.contains("Builder") == true) {
            // If it's a Builder type, just use .build()
            "entry.getValue().build()"
        } else {
            // If it's not a Builder type but needs transformation, use .build().get()
            "entry.getValue().build().get()"
        }
    }

    // Helper to get map collector with merge function and map factory
    private fun getMapCollectorWithMerge(f: FieldModel, keyMapper: String, valueMapper: String): String {
        val mapType = when {
            f.type.contains("HashMap") -> "java.util.HashMap::new"
            f.type.contains("TreeMap") -> "java.util.TreeMap::new"
            f.type.contains("LinkedHashMap") -> "java.util.LinkedHashMap::new"
            else -> "java.util.HashMap::new"
        }

        return "java.util.stream.Collectors.toMap(entry -> $keyMapper, entry -> $valueMapper, (existing, replacement) -> replacement, $mapType)"
    }
    /**
     * Returns the appropriate collector based on the map type to preserve the original map structure
     */
    private fun getMapCollector(f: FieldModel, keyMapper: String, valueMapper: String): String {
        val mapType = f.mapRawType ?: f.fqType?.substringBefore('<') ?: ""

        return when {
            mapType.contains("SortedMap") || mapType.contains("TreeMap") -> {
                "java.util.stream.Collectors.toMap(" +
                        "entry -> $keyMapper, " +
                        "entry -> $valueMapper, " +
                        "(existing, replacement) -> replacement, " +
                        "java.util.TreeMap::new)"
            }

            mapType.contains("LinkedHashMap") -> {
                "java.util.stream.Collectors.toMap(" +
                        "entry -> $keyMapper, " +
                        "entry -> $valueMapper, " +
                        "(existing, replacement) -> replacement, " +
                        "java.util.LinkedHashMap::new)"
            }

            mapType.contains("ConcurrentHashMap") || mapType.contains("ConcurrentMap") -> {
                "java.util.stream.Collectors.toConcurrentMap(" +
                        "entry -> $keyMapper, " +
                        "entry -> $valueMapper)"
            }

            mapType.contains("NavigableMap") -> {
                "java.util.stream.Collectors.toMap(" +
                        "entry -> $keyMapper, " +
                        "entry -> $valueMapper, " +
                        "(existing, replacement) -> replacement, " +
                        "java.util.TreeMap::new)"
            }

            else -> {
                // Default to HashMap for Map, HashMap, and unknown types
                "java.util.stream.Collectors.toMap(" +
                        "entry -> $keyMapper, " +
                        "entry -> $valueMapper)"
            }
        }
    }
}