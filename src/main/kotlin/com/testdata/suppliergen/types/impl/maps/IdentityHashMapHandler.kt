package com.testdata.suppliergen.types.impl.maps


object IdentityHashMapHandler : CommonMapHandler() {
    override val supportedTypes: Set<String>
        get() = setOf("java.util.IdentityHashMap", "IdentityHashMap")
    override fun returnMap() = "java.util.IdentityHashMap"
    override fun constructorMap() = "java.util.IdentityHashMap"
}
