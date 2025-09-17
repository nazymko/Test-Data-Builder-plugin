package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for email fields based on field name patterns.
 * Matches fields like: email, userEmail, customer_email, contactEmail, etc.
 */
object EmailTypeHandler : PatternBasedTypeHandler {

    private val emailPatterns = listOf(
        "email", "e_mail", "e-mail", "mail",
        "contact", "username", "login"
    )

    private val emailDomains = listOf(
        "example.com", "test.com", "sample.org", "demo.net",
        "company.com", "business.org", "enterprise.net", "corp.com"
    )

    private val emailPrefixes = listOf(
        "user", "test", "demo", "sample", "john", "jane", "admin",
        "customer", "client", "member", "person", "account"
    )

    override val priority: Int = 10

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        // Only handle String types for email
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, emailPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val prefix = emailPrefixes.random()
        val domain = emailDomains.random()
        return "\"$prefix@$domain\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val prefix = emailPrefixes.random()
        val number = (1..999).random()
        val domain = emailDomains.random()
        return "\"$prefix$number@$domain\""
    }

    override val staticExtraImports: Set<String> = emptySet()
}