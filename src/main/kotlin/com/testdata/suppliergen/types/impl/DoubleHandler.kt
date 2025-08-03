package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.google.common.util.concurrent.AtomicDouble
import com.intellij.psi.PsiType

object DoubleHandler : TypeHandler {

    private val counter = AtomicDouble(0.2) // start from 1

    override val supportedTypes: Set<String> get() = setOf("double", "java.lang.Double", "Double")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "%.2f".format(counter.getAndAdd(0.71))
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)


    override val staticExtraImports: Set<String> get() = setOf()
}
