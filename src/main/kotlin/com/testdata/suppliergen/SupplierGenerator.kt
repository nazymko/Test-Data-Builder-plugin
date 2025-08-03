package com.testdata.suppliergen.generator

import com.testdata.suppliergen.factory.FieldModelFactory
import com.testdata.suppliergen.generator.sections.*
import com.testdata.suppliergen.model.GenerationResult
import com.testdata.suppliergen.model.SupplierClassModel
import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope

class SupplierGenerator(
    private val project: Project
) {

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

        val baseDir: PsiDirectory = if (context.generateInSinglePackage) {
            context.targetDir ?: error("GenerationContext.targetDir is null.")
        } else {
            targetClass.containingFile.containingDirectory
                ?: error("Could not resolve target class directory.")
        }

        val className = targetClass.qualifiedName ?: return GenerationResult(originalFile, emptyList())
        if (className in context.visited || currentDepth >= context.maxDepth) {
            return GenerationResult(originalFile, emptyList())
        }
        context.visited.add(className)

        val qualifiedName = targetClass.qualifiedName ?: return GenerationResult(originalFile, emptyList())
        val simpleName = targetClass.name ?: return GenerationResult(originalFile, emptyList())

        val fieldModels = targetClass.fields
            .filter { it.containingClass == targetClass }
            .map { FieldModelFactory.from(it) }

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

                val containingFile = resolved.containingFile as? PsiJavaFile ?: continue
                val result = generateRecursively(context, containingFile, resolved, currentDepth + 1)
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
}
