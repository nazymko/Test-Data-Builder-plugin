package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object PassportTypeHandler : PatternBasedTypeHandler {
    private val passportPatterns = listOf("passport", "passportnumber", "passport_number")
    private val passportPrefixes = listOf("P", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    override val priority: Int = 18
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, passportPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${passportPrefixes.random()}${(100000000..999999999).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${passportPrefixes.random()}${(100000000..999999999).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}