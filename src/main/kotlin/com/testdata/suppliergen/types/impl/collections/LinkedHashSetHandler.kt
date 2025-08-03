package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.CommonCollectionHandler
import com.intellij.psi.PsiType

object LinkedHashSetHandler : CommonCollectionHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.LinkedHashSet", "LinkedHashSet")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.LinkedHashSet<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.LinkedHashSet")
}
