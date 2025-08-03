package com.testdata.suppliergen.types.impl.maps


object ConcurrentMapHandler : CommonMapHandler() {
    override val supportedTypes: Set<String>
        get() = setOf("java.util.concurrent.ConcurrentMap", "ConcurrentMap")

    override fun returnMap() = "java.util.concurrent.ConcurrentMap"
    override fun constructorMap() = "java.util.concurrent.ConcurrentHashMap"
}
