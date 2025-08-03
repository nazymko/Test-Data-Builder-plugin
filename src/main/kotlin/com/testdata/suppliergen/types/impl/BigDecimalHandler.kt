package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType
import java.util.concurrent.atomic.AtomicInteger

object BigDecimalHandler : TypeHandler {
    val counter = AtomicInteger(0)

    override val supportedTypes: Set<String> get() = setOf("java.math.BigDecimal", "BigDecimal")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        "BigDecimal.ZERO"

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        "new BigDecimal(${counter.andIncrement})"

    override val staticExtraImports = setOf("java.math.BigDecimal", )
}