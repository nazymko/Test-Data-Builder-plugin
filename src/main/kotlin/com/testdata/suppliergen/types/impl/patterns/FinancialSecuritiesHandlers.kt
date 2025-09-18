package com.testdata.suppliergen.types.impl.patterns

import com.intellij.psi.PsiType
import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import kotlin.random.Random

object ISINTypeHandler : PatternBasedTypeHandler {
    private val isinPatterns =
        listOf("isin", "securityid", "security_id", "instrumentid", "instrument_id", "securitycode", "security_code")

    // Sample country codes for ISIN generation
    private val countryCodes = listOf("US", "GB", "DE", "FR", "CA", "JP", "AU", "CH", "NL", "SE")

    override val priority: Int = 25
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) =
        supports(fqName, psiType) && matchesPattern(fieldName, isinPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val countryCode = countryCodes.random()
        val nsin = generateNSIN()
        val checkDigit = (0..9).random()
        return "\"$countryCode$nsin$checkDigit\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    private fun generateNSIN(): String {
        return (100000000..999999999).random().toString()
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object AskPriceTypeHandler : PatternBasedTypeHandler {
    private val askPatterns =
        listOf("ask", "askprice", "ask_price", "offer", "offerprice", "offer_price", "sellprice", "sell_price")

    override val priority: Int = 24
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String" -> true
        "java.math.BigDecimal", "BigDecimal" -> true
        "java.lang.Double", "Double", "double" -> true
        "java.lang.Float", "Float", "float" -> true
        "java.lang.Long", "Long", "long" -> true
        "java.lang.Integer", "Integer", "int" -> true
        else -> false
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) =
        supports(fqName, psiType) && matchesPattern(fieldName, askPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val basePrice = Random.nextDouble(50.0, 500.0)
        val roundedPrice = String.format("%.2f", basePrice)

        return when (fqName) {
            "java.lang.String", "String" -> {
                // Include currency symbol only for String fields
                if (fieldName.lowercase().contains("price") || fieldName.lowercase().contains("cost") || fieldName.lowercase().contains("amount")) {
                    "\"$$roundedPrice\""
                } else {
                    "\"$roundedPrice\""
                }
            }
            "java.math.BigDecimal", "BigDecimal" -> "new java.math.BigDecimal(\"$roundedPrice\")"
            "java.lang.Double", "Double", "double" -> roundedPrice
            "java.lang.Float", "Float", "float" -> "${roundedPrice}f"
            "java.lang.Long", "Long", "long" -> "${(basePrice * 100).toLong()}L" // Convert to cents
            "java.lang.Integer", "Integer", "int" -> "${(basePrice * 100).toInt()}" // Convert to cents
            else -> roundedPrice
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> = setOf("java.math.BigDecimal")
}

object BidPriceTypeHandler : PatternBasedTypeHandler {
    private val bidPatterns =
        listOf("bid", "bidprice", "bid_price", "buyprice", "buy_price", "purchaseprice", "purchase_price")

    override val priority: Int = 24
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String" -> true
        "java.math.BigDecimal", "BigDecimal" -> true
        "java.lang.Double", "Double", "double" -> true
        "java.lang.Float", "Float", "float" -> true
        "java.lang.Long", "Long", "long" -> true
        "java.lang.Integer", "Integer", "int" -> true
        else -> false
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) =
        supports(fqName, psiType) && matchesPattern(fieldName, bidPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val basePrice = Random.nextDouble(45.0, 495.0) // Slightly lower than ask price
        val roundedPrice = String.format("%.2f", basePrice)

        return when (fqName) {
            "java.lang.String", "String" -> {
                // Include currency symbol only for String fields
                if (fieldName.lowercase().contains("price") || fieldName.lowercase().contains("cost") || fieldName.lowercase().contains("amount")) {
                    "\"$$roundedPrice\""
                } else {
                    "\"$roundedPrice\""
                }
            }
            "java.math.BigDecimal", "BigDecimal" -> "new java.math.BigDecimal(\"$roundedPrice\")"
            "java.lang.Double", "Double", "double" -> roundedPrice
            "java.lang.Float", "Float", "float" -> "${roundedPrice}f"
            "java.lang.Long", "Long", "long" -> "${(basePrice * 100).toLong()}L" // Convert to cents
            "java.lang.Integer", "Integer", "int" -> "${(basePrice * 100).toInt()}" // Convert to cents
            else -> roundedPrice
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> = setOf("java.math.BigDecimal")
}

object YieldTypeHandler : PatternBasedTypeHandler {
    private val yieldPatterns = listOf(
        "yield",
        "yieldrate",
        "yield_rate",
        "dividend",
        "dividendyield",
        "dividend_yield",
        "return",
        "returnrate",
        "return_rate"
    )

    override val priority: Int = 23
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String" -> true
        "java.math.BigDecimal", "BigDecimal" -> true
        "java.lang.Double", "Double", "double" -> true
        "java.lang.Float", "Float", "float" -> true
        "java.lang.Long", "Long", "long" -> true
        "java.lang.Integer", "Integer", "int" -> true
        else -> false
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) =
        supports(fqName, psiType) && matchesPattern(fieldName, yieldPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val yieldRate = Random.nextDouble(0.5, 15.0)
        val roundedYield = String.format("%.2f", yieldRate)

        return when (fqName) {
            "java.lang.String", "String" -> {
                // Include percentage symbol only for String fields that contain "yield", "rate", or "return"
                if (fieldName.lowercase().contains("yield") || fieldName.lowercase().contains("rate") || fieldName.lowercase().contains("return") || fieldName.lowercase().contains("dividend")) {
                    "\"$roundedYield%\""
                } else {
                    "\"$roundedYield\""
                }
            }
            "java.math.BigDecimal", "BigDecimal" -> "new java.math.BigDecimal(\"$roundedYield\")"
            "java.lang.Double", "Double", "double" -> roundedYield
            "java.lang.Float", "Float", "float" -> "${roundedYield}f"
            else -> roundedYield
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> = setOf("java.math.BigDecimal")
}

object TickerSymbolTypeHandler : PatternBasedTypeHandler {
    private val tickerPatterns =
        listOf("ticker", "symbol", "tickersymbol", "ticker_symbol", "stock", "stocksymbol", "stock_symbol")

    // Sample ticker symbols for different markets
    private val sampleTickers = listOf(
        "AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", "META", "NVDA", "BRK.A", "V", "JNJ",
        "VOD.L", "BP.L", "SHEL.L", "AZN.L", "LLOY.L", // London Stock Exchange
        "SAP.DE", "ALV.DE", "SIE.DE", "BAS.DE", "VOW3.DE"
    ) // Frankfurt

    override val priority: Int = 22
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) =
        supports(fqName, psiType) && matchesPattern(fieldName, tickerPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${sampleTickers.random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        "\"${generateRandomTicker()}\""

    private fun generateRandomTicker(): String {
        val length = (3..5).random()
        return (1..length).map { ('A'..'Z').random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object CUSIPTypeHandler : PatternBasedTypeHandler {
    private val cusipPatterns = listOf("cusip", "cusipnumber", "cusip_number", "cusipcode", "cusip_code")

    override val priority: Int = 21
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) =
        supports(fqName, psiType) && matchesPattern(fieldName, cusipPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val issuerCode = (100000..999999).random()
        val issueCode = (10..99).random()
        val checkDigit = (0..9).random()
        return "\"$issuerCode$issueCode$checkDigit\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String> = emptySet()
}