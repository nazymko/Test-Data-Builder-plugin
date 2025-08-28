package com.testdata.suppliergen.generator

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.testdata.suppliergen.factory.FieldModelFactory
import com.testdata.suppliergen.generator.sections.*
import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.model.GenerationResult
import com.testdata.suppliergen.model.SupplierClassModel

class SupplierGenerator(
    private val project: Project
) {
    private val logger = Logger.getInstance(SupplierGenerator::class.java)
    private val libraryClassHandler = LibraryClassHandler(project)

    private val sections: List<SectionBuilder> = listOf(
        PackageSection(),
        ImportSection(),
        ClassHeaderSection(),
        FieldSection(),
        ConfiguredBuilderSection(),
        AdditionalMethodsSection(),
        InitializedSection(),
        AssertEqualSection(),
        GetMethodSection()
    )

    fun generateRecursively(
        context: GenerationContext,
        originalFile: PsiJavaFile,
        targetClass: PsiClass,
        currentDepth: Int = 0
    ): GenerationResult {

        val baseDir: PsiDirectory = resolveBaseDirectory(context, targetClass)

        val className = targetClass.qualifiedName ?: return GenerationResult(originalFile, emptyList())
        if (className in context.visited || currentDepth >= context.maxDepth) {
            return GenerationResult(originalFile, emptyList())
        }
        context.visited.add(className)

        val qualifiedName = targetClass.qualifiedName ?: return GenerationResult(originalFile, emptyList())
        val simpleName = targetClass.name ?: return GenerationResult(originalFile, emptyList())

        val fieldModels = extractFieldModels(targetClass)

        val packageName = JavaDirectoryService.getInstance().getPackage(baseDir)?.qualifiedName
            ?: originalFile.packageName

        val supplierClassName = "${simpleName}Supplier"
        val model = SupplierClassModel(
            packageName = packageName,
            targetClassName = simpleName,
            targetQualifiedName = qualifiedName,
            supplierClassName = supplierClassName,
            fields = fieldModels,
            instantiationMode = context.instantiationMode,
            ctorParamOrder = emptyList(),
            ctx = context
        )

        val rootSupplierFile = generate(model, supplierClassName)
        val dependencies = mutableListOf<PsiJavaFile>()

        val psiFacade = JavaPsiFacade.getInstance(project)
        val scope = GlobalSearchScope.allScope(project)

        for (field in model.fields) {
            val fqTypesToResolve = buildList {
                if (!field.isKnown) add(field.fqType)
                if (field.isOptional && !field.optionalInnerIsKnown) field.optionalInnerType?.let(::add)
                if (field.isCollection && !field.elementIsKnown) field.elementFqType?.let(::add)
                if (field.isMap) {
                    if (!field.keyIsKnown) field.keyType?.let(::add)
                    if (!field.valueIsKnown) field.valueType?.let(::add)
                }
            }

            for (fqType in fqTypesToResolve) {
                val resolved = psiFacade.findClass(fqType ?: continue, scope) ?: continue
                if (resolved.qualifiedName?.startsWith("java.") == true) continue
                if (resolved.qualifiedName in context.visited) continue

                val containingFile = resolved.containingFile as? PsiJavaFile
                val result = if (containingFile != null) {
                    generateRecursively(context, containingFile, resolved, currentDepth + 1)
                } else {
                    // Handle library class without containing file
                    generateForLibraryClass(context, resolved, currentDepth + 1)
                }
                dependencies += result.root
                dependencies += result.dependencies
            }
        }

        return GenerationResult(rootSupplierFile as PsiJavaFile, dependencies)
    }

    fun generate(
        model: SupplierClassModel,
        supplierClassName: String
    ): PsiFile {
        val code = buildJavaText(model)
        return PsiFileFactory.getInstance(project).createFileFromText(
            "$supplierClassName.java", JavaFileType.INSTANCE, code
        )
    }

    private fun buildJavaText(model: SupplierClassModel): String {
        return buildString {
            for (section in sections) {
                appendLine(section.render(model))
            }
            appendLine("}") // close class declaration
        }
    }

    private fun resolveBaseDirectory(context: GenerationContext, targetClass: PsiClass): PsiDirectory {
        return if (context.generateInSinglePackage || libraryClassHandler.isLibraryClass(targetClass)) {
            // Always use target directory for library classes or single package mode
            context.targetDir ?: error("GenerationContext.targetDir is null for library class: ${targetClass.qualifiedName}")
        } else {
            val containingDirectory = targetClass.containingFile?.containingDirectory
            return DirectoryValidator.getSafeDirectory(containingDirectory, context.targetDir)
        }
    }

    private fun extractFieldModels(targetClass: PsiClass): List<FieldModel> {
        return if (libraryClassHandler.isLibraryClass(targetClass)) {
            logger.info("Extracting fields from library class: ${targetClass.qualifiedName}")
            libraryClassHandler.extractFieldsFromLibraryClass(targetClass)
        } else {
            // Standard field extraction for source classes
            targetClass.fields
                .filter { it.containingClass == targetClass }
                .map { FieldModelFactory.from(it) }
        }
    }

    private fun generateForLibraryClass(
        context: GenerationContext,
        libraryClass: PsiClass,
        currentDepth: Int
    ): GenerationResult {
        logger.debug("Generating supplier for library class: ${libraryClass.qualifiedName}")

        val className = libraryClass.qualifiedName ?: return GenerationResult(createEmptyJavaFile(), emptyList())

        if (className in context.visited || currentDepth >= context.maxDepth) {
            logger.debug("Skipping library class generation for $className (already visited or max depth reached)")
            return GenerationResult(createEmptyJavaFile(), emptyList())
        }

        context.visited.add(className)

        val qualifiedName = libraryClass.qualifiedName ?: return GenerationResult(createEmptyJavaFile(), emptyList())
        val simpleName = libraryClass.name ?: return GenerationResult(createEmptyJavaFile(), emptyList())

        val fieldModels = libraryClassHandler.extractFieldsFromLibraryClass(libraryClass)

        // For library classes, use the target directory for package resolution
        val packageName = context.targetDir?.let { dir ->
            JavaDirectoryService.getInstance().getPackage(dir)?.qualifiedName
        } ?: "com.testdata.suppliers" // fallback package

        val supplierClassName = "${simpleName}Supplier"
        val model = SupplierClassModel(
            packageName = packageName,
            targetClassName = simpleName,
            targetQualifiedName = qualifiedName,
            supplierClassName = supplierClassName,
            fields = fieldModels,
            instantiationMode = context.instantiationMode,
            ctorParamOrder = emptyList(),
            ctx = context
        )

        val rootSupplierFile = generate(model, supplierClassName) as PsiJavaFile

        // For library classes, we typically don't generate dependencies recursively 
        // to avoid infinite recursion with library types
        val dependencies = mutableListOf<PsiJavaFile>()

        return GenerationResult(rootSupplierFile, dependencies)
    }

    private fun createEmptyJavaFile(): PsiJavaFile {
        return PsiFileFactory.getInstance(project).createFileFromText(
            "Empty.java", JavaFileType.INSTANCE, "// Empty placeholder"
        ) as PsiJavaFile
    }
}
