package com.testdata.suppliergen.types.impl.maps


object TreeMapHandler : CommonMapHandler() {
    override val supportedTypes: Set<String>
        get() = setOf("java.util.TreeMap", "TreeMap")

    override fun returnMap() = "java.util.TreeMap"
    override fun constructorMap() = "java.util.TreeMap"
}
