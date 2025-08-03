package com.testdata.suppliergen.types.impl.maps


object LinkedHashMapHandler : CommonMapHandler() {
    override val supportedTypes: Set<String>
        get() = setOf("java.util.LinkedHashMap", "LinkedHashMap")

    override fun returnMap() = "java.util.LinkedHashMap"
    override fun constructorMap() = "java.util.LinkedHashMap"
}
