package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.CommonCollectionHandler
import com.intellij.psi.PsiType

object ArrayDequeHandler : CommonCollectionHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.ArrayDeque", "ArrayDeque")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.ArrayDeque<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.ArrayDeque")
}
