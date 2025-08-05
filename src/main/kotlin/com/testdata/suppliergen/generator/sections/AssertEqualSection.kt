package com.testdata.suppliergen.generator.sections

import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.SupplierClassModel

class AssertEqualSection : SectionBuilder {
    override fun render(model: SupplierClassModel): String {
        val lines = model.fields.joinToString("\n") {
            "           assertThat(expected.${it.getter}()).isEqualTo(actual.${it.getter}());"
        }
        return """
        
        public static void assertEqual(${model.targetClassName} expected, ${model.targetClassName} actual) {
$lines
        }
    """ + "\n"
    }
}
