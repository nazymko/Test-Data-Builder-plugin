package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

/**
 * Type handler for department fields based on field name patterns.
 * Matches fields like: department, dept, division, team, departmentName, etc.
 */
object DepartmentTypeHandler : PatternBasedTypeHandler {

    private val departmentPatterns = listOf(
        "department", "dept", "division", "team", "unit", "group"
    )

    private val departments = listOf(
        "Engineering", "Sales", "Marketing", "Human Resources", "Finance",
        "Operations", "Customer Service", "Product Management", "Quality Assurance",
        "Research and Development", "Information Technology", "Legal",
        "Business Development", "Administration", "Accounting", "Procurement",
        "Security", "Training", "Communications", "Strategy"
    )

    override val priority: Int = 7

    override fun supports(fqName: String?, psiType: PsiType?): Boolean {
        return fqName == "java.lang.String" || fqName == "String"
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?): Boolean {
        return supports(fqName, psiType) && matchesPattern(fieldName, departmentPatterns)
    }

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "\"${departments.random()}\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "\"${departments.random()}\""
    }

    override val staticExtraImports: Set<String> = emptySet()
}