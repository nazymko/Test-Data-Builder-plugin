package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.CommonCollectionHandler
import com.intellij.psi.PsiType

object PriorityQueueHandler : CommonCollectionHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.PriorityQueue", "PriorityQueue")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.PriorityQueue<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.PriorityQueue")
}
