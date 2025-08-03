package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object BitSetHandler : TypeHandler {

    override val supportedTypes: Set<String> = setOf("java.util.BitSet", "BitSet")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.BitSet<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.BitSet")
}
