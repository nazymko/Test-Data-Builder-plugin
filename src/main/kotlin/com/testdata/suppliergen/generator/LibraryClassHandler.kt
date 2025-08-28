package com.testdata.suppliergen.generator

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.compiled.ClsClassImpl
import com.testdata.suppliergen.factory.FieldModelFactory
import com.testdata.suppliergen.model.FieldModel

/**
 * Handles generation for library classes and decompiled classes that don't have source code available.
 */
class LibraryClassHandler(private val project: Project) {
    private val logger = Logger.getInstance(LibraryClassHandler::class.java)

    /**
     * Checks if a class is from a library or decompiled (no source directory available).
     */
    fun isLibraryClass(psiClass: PsiClass): Boolean {
        return when {
            // Decompiled classes are typically ClsClassImpl instances
            psiClass is ClsClassImpl -> true
            // Check if containing file has no directory (library classes)
            psiClass.containingFile?.containingDirectory == null -> true
            // Check if it's in a JAR file or library
            psiClass.containingFile?.virtualFile?.path?.contains(".jar!") == true -> true
            // Check for .class files (compiled classes)
            psiClass.containingFile?.virtualFile?.extension == "class" -> true
            // Check if directory is read-only
            isReadOnlyOrLibraryDirectory(psiClass) -> true
            // Additional check for classes in library source roots
            isInLibrarySourceRoot(psiClass) -> true
            else -> false
        }
    }

    private fun isReadOnlyOrLibraryDirectory(psiClass: PsiClass): Boolean {
        val virtualFile = psiClass.containingFile?.virtualFile ?: return false
        val directory = psiClass.containingFile?.containingDirectory?.virtualFile ?: return false
        
        return !directory.isWritable ||
               directory.path.contains("/lib/") ||
               directory.path.contains("\\lib\\") ||
               directory.path.contains("/build/classes/") ||    // More specific - compiled classes only
               directory.path.contains("\\build\\classes\\") ||  // More specific - compiled classes only  
               directory.path.contains("/.gradle/caches/") ||    // More specific - gradle caches only
               directory.path.contains("\\.gradle\\caches\\")    // More specific - gradle caches only
    }

    private fun isInLibrarySourceRoot(psiClass: PsiClass): Boolean {
        val virtualFile = psiClass.containingFile?.virtualFile ?: return false
        val projectFileIndex = com.intellij.openapi.roots.ProjectFileIndex.getInstance(project)
        return projectFileIndex.isInLibrarySource(virtualFile) || projectFileIndex.isInLibraryClasses(virtualFile)
    }

    /**
     * Extracts fields from library classes using reflection-like approach.
     */
    fun extractFieldsFromLibraryClass(psiClass: PsiClass): List<FieldModel> {
        logger.debug("Extracting fields from library class: ${psiClass.qualifiedName}")
        
        val fields = mutableListOf<FieldModel>()
        
        try {
            // Get all fields including inherited ones for library classes
            val allFields = psiClass.allFields.filter { field ->
                // Filter out static fields and constants typically
                !field.hasModifierProperty(PsiModifier.STATIC) && 
                !field.hasModifierProperty(PsiModifier.FINAL) ||
                isAcceptableConstant(field)
            }
            
            for (field in allFields) {
                try {
                    val fieldModel = createFieldModelForLibraryField(field)
                    fields.add(fieldModel)
                    logger.debug("Successfully created field model for: ${field.name}")
                } catch (e: Exception) {
                    logger.warn("Failed to create field model for ${field.name} in ${psiClass.qualifiedName}: ${e.message}")
                    // Create a fallback field model
                    fields.add(createFallbackFieldModel(field))
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to extract fields from library class ${psiClass.qualifiedName}: ${e.message}")
            // Return basic field models based on available getters/setters
            return extractFieldsFromAccessors(psiClass)
        }
        
        return fields
    }

    private fun isAcceptableConstant(field: PsiField): Boolean {
        // Accept some final fields that might be configurable
        val type = field.type.canonicalText
        return when {
            type.startsWith("java.lang.String") -> true
            type in setOf("int", "long", "double", "boolean", "java.lang.Integer", "java.lang.Long", "java.lang.Double", "java.lang.Boolean") -> true
            else -> false
        }
    }

    private fun createFieldModelForLibraryField(field: PsiField): FieldModel {
        return try {
            FieldModelFactory.from(field)
        } catch (e: Exception) {
            logger.debug("Standard field creation failed for ${field.name}, using library-specific approach")
            createLibrarySpecificFieldModel(field)
        }
    }

    private fun createLibrarySpecificFieldModel(field: PsiField): FieldModel {
        val psiType = field.type
        val fqName = psiType.canonicalText
        val name = field.name
        val type = psiType.presentableText
        
        // Use a simpler approach for library classes
        val handler = com.testdata.suppliergen.types.TypeHandlerRegistry.resolve(fqName, psiType, "Library class field: ${name}")
        val defaultValue = handler.defaultValue(name, fqName, psiType)
        val randomizedValue = handler.randomizedValue(name, fqName, psiType)
        
        val cap = name.replaceFirstChar { it.uppercaseChar() }
        val getter = "${handler.getterPrefix(fqName, psiType)}$cap"
        val setter = "set$cap"

        return FieldModel(
            name = name,
            typeHandler = handler,
            type = type,
            fqType = fqName,
            defaultValue = defaultValue,
            randomizedValue = randomizedValue,
            getter = getter,
            setter = setter,
            isKnown = handler.isKnown,
            extraImports = handler.staticExtraImports + handler.customImports(fqName, psiType),
            psiType = psiType,
            psiClass = null, // Library classes may not have accessible PsiClass
            // Simplified for library classes - no complex collection/optional analysis
            isCollection = isCollectionType(fqName),
            isOptional = isOptionalType(fqName),
            isMap = isMapType(fqName)
        )
    }

    private fun createFallbackFieldModel(field: PsiField): FieldModel {
        val name = field.name
        val type = "Object" // Fallback to Object if type resolution fails
        val fqName = "java.lang.Object"
        
        logger.info("Creating fallback field model for: $name")
        
        return FieldModel(
            name = name,
            typeHandler = com.testdata.suppliergen.types.impl.ObjectHandler,
            type = type,
            fqType = fqName,
            defaultValue = "new Object()",
            randomizedValue = "new Object()",
            getter = "get${name.replaceFirstChar { it.uppercaseChar() }}",
            setter = "set${name.replaceFirstChar { it.uppercaseChar() }}",
            isKnown = true,
            extraImports = emptySet(),
            psiType = field.type,
            psiClass = null
        )
    }

    /**
     * Extract fields by analyzing getter/setter methods when direct field access fails.
     */
    private fun extractFieldsFromAccessors(psiClass: PsiClass): List<FieldModel> {
        logger.debug("Extracting fields from accessors for: ${psiClass.qualifiedName}")
        
        val fieldMap = mutableMapOf<String, FieldInfo>()
        
        // Analyze all methods to find getters and setters
        for (method in psiClass.allMethods) {
            when {
                isGetter(method) -> {
                    val fieldName = extractFieldNameFromGetter(method)
                    val fieldInfo = fieldMap.getOrPut(fieldName) { FieldInfo(fieldName) }
                    fieldInfo.getter = method
                    fieldInfo.type = method.returnType
                }
                isSetter(method) -> {
                    val fieldName = extractFieldNameFromSetter(method)
                    val fieldInfo = fieldMap.getOrPut(fieldName) { FieldInfo(fieldName) }
                    fieldInfo.setter = method
                    if (fieldInfo.type == null && method.parameterList.parameters.isNotEmpty()) {
                        fieldInfo.type = method.parameterList.parameters[0].type
                    }
                }
            }
        }
        
        return fieldMap.values.mapNotNull { fieldInfo ->
            try {
                createFieldModelFromAccessors(fieldInfo)
            } catch (e: Exception) {
                logger.warn("Failed to create field model from accessors for ${fieldInfo.name}: ${e.message}")
                null
            }
        }
    }

    private fun createFieldModelFromAccessors(fieldInfo: FieldInfo): FieldModel? {
        val type = fieldInfo.type ?: return null
        val fqName = type.canonicalText
        val name = fieldInfo.name
        val presentableType = type.presentableText
        
        val handler = com.testdata.suppliergen.types.TypeHandlerRegistry.resolve(fqName, type, "Accessor-based field: $name")
        val defaultValue = handler.defaultValue(name, fqName, type)
        val randomizedValue = handler.randomizedValue(name, fqName, type)
        
        return FieldModel(
            name = name,
            typeHandler = handler,
            type = presentableType,
            fqType = fqName,
            defaultValue = defaultValue,
            randomizedValue = randomizedValue,
            getter = fieldInfo.getter?.name ?: "get${name.replaceFirstChar { it.uppercaseChar() }}",
            setter = fieldInfo.setter?.name ?: "set${name.replaceFirstChar { it.uppercaseChar() }}",
            isKnown = handler.isKnown,
            extraImports = handler.staticExtraImports + handler.customImports(fqName, type),
            psiType = type,
            psiClass = null,
            isCollection = isCollectionType(fqName),
            isOptional = isOptionalType(fqName),
            isMap = isMapType(fqName)
        )
    }

    private fun isGetter(method: PsiMethod): Boolean {
        val name = method.name
        return (name.startsWith("get") || name.startsWith("is")) && 
               method.parameterList.parametersCount == 0 && 
               method.returnType?.equalsToText("void") != true &&
               !method.hasModifierProperty(PsiModifier.STATIC)
    }

    private fun isSetter(method: PsiMethod): Boolean {
        return method.name.startsWith("set") && 
               method.parameterList.parametersCount == 1 && 
               method.returnType?.equalsToText("void") == true &&
               !method.hasModifierProperty(PsiModifier.STATIC)
    }

    private fun extractFieldNameFromGetter(method: PsiMethod): String {
        val name = method.name
        return when {
            name.startsWith("get") -> name.substring(3).replaceFirstChar { it.lowercaseChar() }
            name.startsWith("is") -> name.substring(2).replaceFirstChar { it.lowercaseChar() }
            else -> name
        }
    }

    private fun extractFieldNameFromSetter(method: PsiMethod): String {
        val name = method.name
        return if (name.startsWith("set")) {
            name.substring(3).replaceFirstChar { it.lowercaseChar() }
        } else name
    }

    private fun isCollectionType(fqName: String): Boolean {
        return fqName.startsWith("java.util.Collection") || 
               fqName.startsWith("java.util.List") || 
               fqName.startsWith("java.util.Set") || 
               fqName.startsWith("java.util.Queue")
    }

    private fun isOptionalType(fqName: String): Boolean {
        return fqName.startsWith("java.util.Optional")
    }

    private fun isMapType(fqName: String): Boolean {
        return fqName.startsWith("java.util.Map")
    }

    private data class FieldInfo(
        val name: String,
        var type: PsiType? = null,
        var getter: PsiMethod? = null,
        var setter: PsiMethod? = null
    )
}