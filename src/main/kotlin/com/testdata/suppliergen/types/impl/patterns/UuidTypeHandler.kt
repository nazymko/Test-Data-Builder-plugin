package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object UuidTypeHandler : PatternBasedTypeHandler {
    private val uuidPatterns = listOf("uuid", "guid", "identifier", "id")
    private val hexChars = "0123456789abcdef"
    override val priority: Int = 4
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, uuidPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = generateUuid()
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = generateUuid()
    private fun generateUuid(): String {
        fun randomHex(length: Int) = (1..length).map { hexChars.random() }.joinToString("")
        return "\"${randomHex(8)}-${randomHex(4)}-${randomHex(4)}-${randomHex(4)}-${randomHex(12)}\""
    }
    override val staticExtraImports: Set<String> = emptySet()
}