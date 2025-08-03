package com.testdata.suppliergen.generator.sections

import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.SupplierClassModel
import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.contract.HelperMethodMetadata

class ConfiguredBuilderSection : SectionBuilder {

    override fun render(model: SupplierClassModel): String {
        val lines = model.fields.joinToString("\n") {
            "            .${it.name}(${it.defaultValue})"
        }

        // Recursive flattening of HelperMethodMetadata + dependencies
        fun collectAllHelpers(helper: HelperMethodMetadata?): List<HelperMethodMetadata> {
            if (helper == null) return emptyList()
            val directDeps = helper.dependencies.orEmpty().filterNotNull()
            return listOf(helper) + directDeps.flatMap { collectAllHelpers(it) }
        }

        val allHelpers = model.fields
            .mapNotNull { field ->
                val handler = TypeHandlerRegistry.resolve(field.fqType, field.psiType, "ConfiguredBuilderSection render")
                handler.helperMethod(field,model.ctx)
            }
            .flatMap { collectAllHelpers(it) }
            .distinctBy { it.name }

        val helperMethodsBody = allHelpers.joinToString("\n\n") { it.body }

        return """
            public static ${model.supplierClassName}.${model.supplierClassName}Builder configuredBuilder() {
                return ${model.supplierClassName}.builder()
        $lines;
            }
            
            $helperMethodsBody
        """.trimIndent() + "\n"
    }
}