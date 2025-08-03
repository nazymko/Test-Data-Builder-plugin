package com.testdata.suppliergen.types.impl.maps


object HashMapHandler : CommonMapHandler() {
    override fun returnMap() = "java.util.HashMap"
    override fun constructorMap() = "java.util.HashMap"
    override val supportedTypes: Set<String>
        get() = setOf("java.util.HashMap", "HashMap")
}
