package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.types.contract.HelperMethodMetadata
import com.intellij.psi.PsiType

object StackHandler : CommonCollectionHandler() {
    override fun helperMethod(model: FieldModel, ctx: GenerationContext): HelperMethodMetadata? {
        return super.helperMethod(model, ctx)
    }

    override val supportedTypes: Set<String> = setOf("java.util.Stack", "Stack")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.Stack<>()"

    override val staticExtraImports: Set<String>
        get() = setOf(
            "java.util.Stack"
        )


}
