package com.testdata.suppliergen.generator.sections

import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.SupplierClassModel

class PackageSection : SectionBuilder {
    override fun render(model: SupplierClassModel): String =
        if (model.packageName.isNotBlank()) "package ${model.packageName};\n\n" else ""
}
