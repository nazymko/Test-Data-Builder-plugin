package com.testdata.suppliergen.generator

import com.testdata.suppliergen.model.SupplierClassModel

interface SectionBuilder {
    fun render(model: SupplierClassModel): String
}
