package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.maps.CommonMapHandler
import com.intellij.psi.PsiType

object NavigableMapHandler : CommonMapHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.NavigableMap", "NavigableMap")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.TreeMap()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.NavigableMap")
}
