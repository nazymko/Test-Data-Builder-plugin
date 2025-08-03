package com.testdata.suppliergen.types.impl.maps

import com.intellij.psi.PsiType

object ListMultimapHandlerHandler : CommonMapHandler() {

    override val supportedTypes: Set<String> = setOf("com.google.common.collect.ListMultimap", "ListMultimap")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new com.google.common.collect.ListMultimap<>();"

    override val staticExtraImports: Set<String> get() = setOf("com.google.common.collect.ListMultimap")
}