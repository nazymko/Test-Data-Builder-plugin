package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import kotlin.random.Random

object GDPRConsentHandler : PatternBasedTypeHandler {
    private val gdprPatterns = listOf("gdpr", "consent", "consentid", "consent_id", "privacy", "privacyconsent", "privacy_consent", "dataprotection", "data_protection")

    override val priority: Int = 18
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, gdprPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val currentDate = java.time.LocalDate.now()
        val consentId = when {
            fieldName.lowercase().contains("gdpr") -> "GDPR-CONSENT-${currentDate.year}${String.format("%02d%02d", currentDate.monthValue, currentDate.dayOfMonth)}-${generateAlphaNumeric(8)}"
            fieldName.lowercase().contains("privacy") -> "PRIVACY-${generateAlphaNumeric(12)}"
            else -> "CONSENT-${currentDate.year}-${generateAlphaNumeric(10)}"
        }
        return "\"$consentId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate")
}

object ComplianceIdHandler : PatternBasedTypeHandler {
    private val compliancePatterns = listOf("compliance", "complianceid", "compliance_id", "audit", "auditid", "audit_id", "regulation", "regulatory")

    override val priority: Int = 17
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, compliancePatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val standards = listOf("SOX", "HIPAA", "PCI-DSS", "ISO27001", "SOC2", "GDPR", "CCPA")
        val complianceId = when {
            fieldName.lowercase().contains("audit") -> "AUDIT-${java.time.LocalDate.now().year}-${String.format("%06d", (1..999999).random())}"
            fieldName.lowercase().contains("regulation") -> "REG-${standards.random()}-${generateAlphaNumeric(8)}"
            else -> "COMPLIANCE-${standards.random()}-${generateAlphaNumeric(6)}"
        }
        return "\"$complianceId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate")
}

object LegalDocumentHandler : PatternBasedTypeHandler {
    private val legalPatterns = listOf("contract", "contractid", "contract_id", "agreement", "agreementid", "agreement_id", "policy", "policyid", "policy_id", "terms", "termsid", "terms_id")

    override val priority: Int = 16
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, legalPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val docId = when {
            fieldName.lowercase().contains("contract") -> "CONTRACT-${java.time.LocalDate.now().year}-${String.format("%06d", (1..999999).random())}"
            fieldName.lowercase().contains("agreement") -> "AGREEMENT-${generateVersionString()}-${generateAlphaNumeric(6)}"
            fieldName.lowercase().contains("policy") -> "POLICY-v${(1..9).random()}.${(0..9).random()}-${java.time.LocalDate.now().year}"
            fieldName.lowercase().contains("terms") -> "TERMS-v${(1..9).random()}.${(0..9).random()}.${(0..9).random()}"
            else -> "LEGAL-DOC-${generateAlphaNumeric(10)}"
        }
        return "\"$docId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    private fun generateVersionString(): String {
        return "v${(1..5).random()}.${(0..9).random()}.${(0..9).random()}"
    }

    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate")
}

object CertificationHandler : PatternBasedTypeHandler {
    private val certificationPatterns = listOf("certificate", "certification", "certid", "cert_id", "license", "licenseid", "license_id", "accreditation", "credential")

    override val priority: Int = 15
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, certificationPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val certTypes = listOf("AWS", "AZURE", "GCP", "CISSP", "CISA", "CISM", "PMP", "ITIL", "SANS", "EC-COUNCIL")
        val certId = when {
            fieldName.lowercase().contains("license") -> "LICENSE-${certTypes.random()}-${(100000..999999).random()}"
            fieldName.lowercase().contains("accreditation") -> "ACCRED-${certTypes.random()}-${generateAlphaNumeric(8)}"
            fieldName.lowercase().contains("credential") -> "CRED-${certTypes.random()}-${generateAlphaNumeric(6)}"
            else -> "CERT-${certTypes.random()}-${java.time.LocalDate.now().year}-${(1000..9999).random()}"
        }
        return "\"$certId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate")
}