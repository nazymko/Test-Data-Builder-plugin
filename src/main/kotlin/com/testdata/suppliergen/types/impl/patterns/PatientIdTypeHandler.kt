package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object PatientIdTypeHandler : PatternBasedTypeHandler {
    private val patientPatterns = listOf("patient", "patientid", "patient_id", "medicalid", "medical_id", "healthid", "health_id", "mrn")
    override val priority: Int = 20
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, patientPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = when {
        fieldName.lowercase().contains("mrn") -> "\"MRN-${(100000..999999).random()}\""
        fieldName.lowercase().contains("health") -> "\"HID-${(100000..999999).random()}\""
        else -> "\"PT-${(100000..999999).random()}\""
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}