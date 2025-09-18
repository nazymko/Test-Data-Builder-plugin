package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object BloodTypeTypeHandler : PatternBasedTypeHandler {
    private val bloodPatterns = listOf("blood", "bloodtype", "blood_type", "bloodgroup", "blood_group")
    private val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    override val priority: Int = 18
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, bloodPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${bloodTypes.random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${bloodTypes.random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}