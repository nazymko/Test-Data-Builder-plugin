package com.testdata.suppliergen.types.contract

data class HelperMethodMetadata(
    val body: String,
    val name: String,
    val dependencies: List<HelperMethodMetadata?>?,
)