package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object DriverLicenseTypeHandler : PatternBasedTypeHandler {
    private val licensePatterns = listOf("license", "driverlicense", "driver_license", "dl", "licensenumber")
    override val priority: Int = 17
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, licensePatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"DL${(100000000..999999999).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"D${(1000..9999).random()}-${(100..999).random()}-${(10..99).random()}-${(100..999).random()}-${(1..9).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}