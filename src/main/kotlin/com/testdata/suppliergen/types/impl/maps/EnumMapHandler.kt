package com.testdata.suppliergen.types.impl.maps


object EnumMapHandler : CommonMapHandler() {
    override fun returnMap() = "java.util.EnumMap"
    override fun constructorMap() = "java.util.EnumMap"
    override val supportedTypes: Set<String>
        get() = setOf("java.util.EnumMap", "EnumMap")

    override fun optionalMapArgs(mapType: String?, keyType: String?, valueType: String?): String {
        return if (keyType != null) "$keyType.class" else ""
    }

}
