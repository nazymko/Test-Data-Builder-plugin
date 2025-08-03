package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object UnknownPojoHandler : TypeHandler {

    override fun supports(fqName: String?, psiType: PsiType?): Boolean = true

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val simple = fqName?.substringAfterLast('.')
            ?: psiType?.presentableText
            ?: "Unknown"
        if(simple== "Unknown") {
            return "new Object()"
        }
        return "${simple}Supplier.configuredBuilder()"
    }

    override val isKnown: Boolean = false
}