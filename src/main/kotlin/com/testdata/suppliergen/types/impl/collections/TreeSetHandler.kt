package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.CommonCollectionHandler
import com.intellij.psi.PsiType

object TreeSetHandler : CommonCollectionHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.TreeSet", "TreeSet")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.TreeSet<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.TreeSet")
}
