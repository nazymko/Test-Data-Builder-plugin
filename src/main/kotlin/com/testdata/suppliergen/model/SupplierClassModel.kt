package com.testdata.suppliergen.model

import com.testdata.suppliergen.generator.GenerationContext

data class SupplierClassModel(
    val packageName: String,
    val targetClassName: String,
    val supplierClassName: String,
    val targetQualifiedName: String,
    val fields: List<FieldModel>,
    val imports: Set<String> = linkedSetOf(),

    // how to build result in get()
    val instantiationMode: InstantiationMode = InstantiationMode.SETTERS,

    // only for CONSTRUCTOR mode (if you can detect / configure it)
    val ctorParamOrder: List<FieldModel> = emptyList(),

    // if you can detect Lombok/own builder presence
    val hasBuilder: Boolean = false,
    // Configuration of the task
    val ctx: GenerationContext,
    val forcedFqn : MutableList<String> = mutableListOf()
)
