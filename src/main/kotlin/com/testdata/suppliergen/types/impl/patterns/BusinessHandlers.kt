package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object SkuTypeHandler : PatternBasedTypeHandler {
    private val skuPatterns = listOf("sku", "productcode", "product_code", "itemnumber", "item_number", "barcode")
    override val priority: Int = 11
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, skuPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"SKU-${(10000..99999).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"PROD-${('A'..'Z').random()}${('A'..'Z').random()}${('A'..'Z').random()}${(100..999).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}

object OrderNumberTypeHandler : PatternBasedTypeHandler {
    private val orderPatterns = listOf("order", "ordernumber", "order_number", "purchaseorder", "purchase_order", "po")
    override val priority: Int = 10
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, orderPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = when {
        fieldName.lowercase().contains("po") || fieldName.lowercase().contains("purchase") -> "\"PO-${(100000..999999).random()}\""
        else -> "\"ORD-${java.time.LocalDate.now().year}${String.format("%03d", (1..999).random())}\""
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate")
}

object CustomerIdTypeHandler : PatternBasedTypeHandler {
    private val customerPatterns = listOf("customer", "customerid", "customer_id", "clientid", "client_id", "client")
    override val priority: Int = 9
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, customerPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = when {
        fieldName.lowercase().contains("client") -> "\"CLI-${(100000..999999).random()}\""
        else -> "\"CUST-${(100000..999999).random()}\""
    }
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}