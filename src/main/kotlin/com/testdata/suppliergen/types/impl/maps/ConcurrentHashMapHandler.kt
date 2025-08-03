package com.testdata.suppliergen.types.impl.maps


object ConcurrentHashMapHandler : CommonMapHandler() {
    override val supportedTypes: Set<String>
        get() = setOf("java.util.concurrent.ConcurrentHashMap", "ConcurrentHashMap")
    override fun returnMap() = "java.util.concurrent.ConcurrentHashMap"
    override fun constructorMap() = "java.util.concurrent.ConcurrentHashMap"
}
