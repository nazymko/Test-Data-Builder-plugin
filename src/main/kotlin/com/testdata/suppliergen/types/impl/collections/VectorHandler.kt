package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.CommonCollectionHandler
import com.intellij.psi.PsiType

object VectorHandler : CommonCollectionHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.Vector", "Vector")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.Vector<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.Vector")
}
