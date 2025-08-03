package com.testdata.suppliergen.types.impl.maps


object SortedMapHandler : CommonMapHandler() {
    override fun returnMap() = "java.util.SortedMap"
    override fun constructorMap() = "java.util.TreeMap"
    override val supportedTypes: Set<String>
        get() = setOf("java.util.SortedMap", "SortedMap")
    override val staticExtraImports: Set<String>
        get() = super.staticExtraImports + setOf("java.util.TreeMap", "java.util.SortedMap")
}
