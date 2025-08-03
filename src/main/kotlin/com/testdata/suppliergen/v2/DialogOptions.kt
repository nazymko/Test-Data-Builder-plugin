package com.testdata.suppliergen.v2

import com.testdata.suppliergen.SupplierOptionsDialog
import com.testdata.suppliergen.model.InstantiationMode

data class DialogOptions(
    val selectedSourceRoot: String,
    val shouldGenerateInSinglePackage: Boolean,
    val instantiationMode: InstantiationMode,
    val testData: SupplierOptionsDialog.TestDataClassSelection?,
    val maxDepth: Int
)