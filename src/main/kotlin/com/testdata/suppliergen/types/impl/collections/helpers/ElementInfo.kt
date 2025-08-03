package com.testdata.suppliergen.types.impl.collections.helpers

// Inspection utility (moved from original CollectionHandler)
data class ElementInfo(
    val rawType: String,
    val elementPresentableType: String?,
    val elementFqType: String?,
    val elementIsKnown: Boolean
)