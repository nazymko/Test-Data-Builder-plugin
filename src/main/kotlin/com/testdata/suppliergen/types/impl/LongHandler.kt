package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType
import java.util.concurrent.atomic.AtomicLong

object LongHandler : TypeHandler {
    override val supportedTypes: Set<String> get() = setOf("long", "java.lang.Long", "Long")

    private val counter = AtomicLong(1) // start from 1


    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val next = counter.getAndIncrement()
        return "%sL".format(next.toString())
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        defaultValue(fieldName, fqName, psiType)

}