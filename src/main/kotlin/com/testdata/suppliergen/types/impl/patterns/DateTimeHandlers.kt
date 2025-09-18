package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import java.time.LocalDate

object ExpirationDateTypeHandler : PatternBasedTypeHandler {
    private val expirationPatterns = listOf("expiration", "expires", "expiry", "validuntil", "valid_until", "expirydate", "expiry_date")
    override val priority: Int = 8
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String", "java.time.LocalDate", "LocalDate", "java.util.Date", "Date" -> true
        else -> false
    }
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, expirationPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val futureDate = LocalDate.now().plusMonths((6..36).random().toLong())
        return when (fqName) {
            "java.lang.String", "String" -> "\"${String.format("%02d/%02d", futureDate.monthValue, futureDate.year % 100)}\""
            "java.time.LocalDate", "LocalDate" -> "java.time.LocalDate.of(${futureDate.year}, ${futureDate.monthValue}, ${futureDate.dayOfMonth})"
            else -> "new java.util.Date(${futureDate.toEpochDay() * 86400000L}L)"
        }
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate", "java.util.Date")
}

object TimeZoneTypeHandler : PatternBasedTypeHandler {
    private val timezonePatterns = listOf("timezone", "time_zone", "tz", "zone")
    private val timezones = listOf("America/New_York", "America/Los_Angeles", "Europe/London", "Europe/Kyiv", "Europe/Paris", "Asia/Tokyo", "Asia/Shanghai", "Australia/Sydney", "UTC")
    override val priority: Int = 7
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, timezonePatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${timezones.random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${timezones.random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}

object MacAddressTypeHandler : PatternBasedTypeHandler {
    private val macPatterns = listOf("mac", "macaddress", "mac_address", "hardware", "networkid", "network_id")
    override val priority: Int = 6
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, macPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        "\"${String.format("%02X:%02X:%02X:%02X:%02X:%02X", (0..255).random(), (0..255).random(), (0..255).random(), (0..255).random(), (0..255).random(), (0..255).random())}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object DomainTypeHandler : PatternBasedTypeHandler {
    private val domainPatterns = listOf("domain", "website", "site", "url", "hostname")
    private val domains = listOf("example.com", "testsite.org", "demo.net", "sample.co.uk", "company.biz", "business.info")
    override val priority: Int = 5
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, domainPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${domains.random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${"test${(100..999).random()}"}.${listOf("com", "org", "net", "co.uk").random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}