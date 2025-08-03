package com.testdata.suppliergen.types.impl.maps

import com.intellij.psi.PsiType

object ListMultimapHandler : CommonMapHandler() {

    override val supportedTypes: Set<String> = setOf("com.google.common.collect.ListMultimap", "ListMultimap")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        "com.google.common.collect.ArrayListMultimap.create()"

    override fun returnMap(): String = "com.google.common.collect.ListMultimap"

    override fun constructorMap(): String = "com.google.common.collect.ArrayListMultimap"
    override fun classCreator(): String = "com.google.common.collect.ArrayListMultimap.create()"


    override val staticExtraImports: Set<String>
        get() = setOf(
            "com.google.common.collect.ListMultimap",
            "com.google.common.collect.ArrayListMultimap"
        )
}