package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object NavigableSetHandler : TypeHandler {

    override val supportedTypes: Set<String> = setOf("java.util.NavigableSet", "NavigableSet")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.NavigableSet<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.NavigableSet")
}
