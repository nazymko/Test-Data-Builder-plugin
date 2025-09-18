package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object NationalIdTypeHandler : PatternBasedTypeHandler {
    private val nationalIdPatterns = listOf("nationalid", "national_id", "citizenid", "citizen_id", "personalid", "personal_id", "idnumber", "id_number")
    override val priority: Int = 16
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, nationalIdPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"ID${(100000000..999999999).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"CID-${(1000000000L..9999999999L).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}