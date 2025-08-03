package com.testdata.suppliergen.types.impl.maps

import com.intellij.psi.PsiType

object ConcurrentSkipListMapHandler : CommonMapHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.concurrent.ConcurrentSkipListMap", "ConcurrentSkipListMap")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "new java.util.concurrent.ConcurrentSkipListMap()"

    override fun returnMap(): String = "java.util.concurrent.ConcurrentSkipListMap"

    override fun constructorMap(): String = "java.util.concurrent.ConcurrentSkipListMap"


    override val staticExtraImports: Set<String>
        get() = setOf(
            "java.util.concurrent.ConcurrentSkipListMap"
        )
}