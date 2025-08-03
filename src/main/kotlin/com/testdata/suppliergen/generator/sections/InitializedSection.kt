package com.testdata.suppliergen.generator.sections

import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.SupplierClassModel

class InitializedSection : SectionBuilder {
    override fun render(model: SupplierClassModel): String = """
        
        public static ${model.targetClassName} initialized() {
            return configuredBuilder().build().get();
        }
    """.trimIndent() + "\n"
}
