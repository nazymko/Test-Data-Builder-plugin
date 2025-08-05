package com.testdata.suppliergen.generator.sections

import com.intellij.psi.PsiClassType
import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.SupplierClassModel

class ImportSection : SectionBuilder {
    override fun render(model: SupplierClassModel): String {
        val baseImports = sequenceOf(
            "java.util.function.Supplier",
            "lombok.Data",
            "lombok.Builder",
            "javax.annotation.processing.Generated",
            "static org.assertj.core.api.Assertions.assertThat"
        )

        val targetClassImport = if (model.packageName.isNotBlank()) {
            sequenceOf("${model.packageName}.${model.targetClassName}")
        } else {
            emptySequence()
        }

        // Collect all extra imports from fields
        val fieldsExtraImports = model.fields.asSequence()
            .flatMap {
                it.extraImports.asSequence()
            }

        val fieldsAsImports: Set<String> = model.fields.asSequence()
            .mapNotNull { field ->
                (field.psiType as? PsiClassType)?.resolve()?.qualifiedName
            }
            .toSet()

        val unknownFieldImports = model.fields
            .asSequence()
            .flatMap { field ->
                buildSet {
                    if (!field.isKnown && field.fqType.isNotBlank()) {
                        if (!model.ctx.generateInSinglePackage) {
                            add(field.fqType + "Supplier")
                        }
                    }

                }
            }

        val allImports =
            (baseImports + model.imports.asSequence()
                    + targetClassImport
                    + fieldsExtraImports
                    + fieldsAsImports
                    + unknownFieldImports)
                .distinct()
                .sorted()

        // Step 1: group imports by simple class name
        val collisions: Map<String, List<String>> = allImports
            .groupBy { it.substringAfterLast('.') }
            .filterValues { it.size > 1 }

        // Step 2: flatten the full FQNs that are colliding
        val classNameCollisions: List<String> = collisions.values.flatten()

        // Step 3: Store fully-qualified names to avoid short imports
        model.forcedFqn.addAll(classNameCollisions)

        // Step 4: Filter out imports with colliding class names (we'll use FQN in code instead)
        val simpleNamesWithCollision = collisions.keys
        val finalImports = allImports.filterNot { it.substringAfterLast('.') in simpleNamesWithCollision }

        // Step 5: Format imports
        val joinedImports = finalImports
            .filter { it.isNotBlank() }
            .filter { it.contains('.') } // Exclude raw types like "List" or "Map"
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filter { it !in model.forcedFqn } // Exclude forced FQ
            .distinct()
            .sorted()
            .joinToString(separator = "\n", postfix = "\n\n") { "import $it;" }
        return joinedImports
    }
}