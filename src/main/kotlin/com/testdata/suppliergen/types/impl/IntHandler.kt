package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType
import java.util.concurrent.atomic.AtomicInteger

object IntHandler : TypeHandler {
    private val counter = AtomicInteger(1) // start from 1

    override val supportedTypes: Set<String> get() = setOf("int", "java.lang.Integer", "Integer")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val next = counter.getAndIncrement()
        return next.toString()
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)

}