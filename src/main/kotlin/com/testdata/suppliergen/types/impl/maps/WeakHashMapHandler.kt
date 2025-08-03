package com.testdata.suppliergen.types.impl.maps


object WeakHashMapHandler : CommonMapHandler() {
    override val supportedTypes: Set<String>
        get() = setOf("java.util.WeakHashMap", "WeakHashMap")
    override fun returnMap() = "java.util.WeakHashMap"
    override fun constructorMap() = "java.util.WeakHashMap"
}
