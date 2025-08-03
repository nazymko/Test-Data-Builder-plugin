package com.testdata.suppliergen.model

enum class InstantiationMode {
    SETTERS,      // new T(); then setters
    BUILDER,      // T.builder()....
    CONSTRUCTOR   // new T(arg1, arg2, ...)
}