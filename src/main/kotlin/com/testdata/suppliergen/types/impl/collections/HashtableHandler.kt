package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.maps.CommonMapHandler
import com.intellij.psi.PsiType

object HashtableHandler : CommonMapHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.Hashtable", "Hashtable")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.Hashtable<>()"

    override val staticExtraImports: Set<String> get() = setOf("java.util.Hashtable")
}
