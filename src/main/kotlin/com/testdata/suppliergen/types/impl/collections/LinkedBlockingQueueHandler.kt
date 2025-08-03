package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.CommonCollectionHandler
import com.intellij.psi.PsiType

object LinkedBlockingQueueHandler : CommonCollectionHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.concurrent.LinkedBlockingQueue", "LinkedBlockingQueue")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.concurrent.LinkedBlockingQueue<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.concurrent.LinkedBlockingQueue")
}
