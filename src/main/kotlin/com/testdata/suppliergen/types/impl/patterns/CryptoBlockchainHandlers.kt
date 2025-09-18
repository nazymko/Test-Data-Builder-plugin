package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import kotlin.random.Random

object BitcoinAddressHandler : PatternBasedTypeHandler {
    private val bitcoinPatterns = listOf("bitcoin", "btc", "bitcoinaddress", "bitcoin_address", "btcaddress", "btc_address", "wallet", "walletaddress")

    override val priority: Int = 26
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, bitcoinPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        // Generate P2PKH address (starts with 1)
        val address = "1" + generateRandomString(33, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")
        return "\"$address\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("segwit") -> {
                // Bech32 address (starts with bc1)
                val address = "bc1" + generateRandomString(39, "023456789acdefghjklmnpqrstuvwxyz")
                "\"$address\""
            }
            Random.nextBoolean() -> {
                // P2SH address (starts with 3)
                val address = "3" + generateRandomString(33, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")
                "\"$address\""
            }
            else -> defaultValue(fieldName, fqName, psiType)
        }
    }

    private fun generateRandomString(length: Int, chars: String): String {
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object EthereumAddressHandler : PatternBasedTypeHandler {
    private val ethereumPatterns = listOf("ethereum", "eth", "ethereumaddress", "ethereum_address", "ethaddress", "eth_address", "contract", "contractaddress")

    override val priority: Int = 26
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, ethereumPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val address = "0x" + generateHexString(40)
        return "\"$address\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateHexString(length: Int): String {
        val hexChars = "0123456789abcdef"
        return (1..length).map { hexChars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object WalletIdHandler : PatternBasedTypeHandler {
    private val walletPatterns = listOf("walletid", "wallet_id", "cryptowallet", "crypto_wallet", "digitalwallet", "digital_wallet")

    override val priority: Int = 25
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, walletPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val walletId = "WALLET-${generateAlphaNumeric(8)}-${generateAlphaNumeric(8)}"
        return "\"$walletId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val prefixes = listOf("WALLET", "CRYPTO", "DIGITAL", "VAULT", "PURSE")
        val prefix = prefixes.random()
        val walletId = "$prefix-${generateAlphaNumeric(12)}"
        return "\"$walletId\""
    }

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object BlockchainHashHandler : PatternBasedTypeHandler {
    private val hashPatterns = listOf("hash", "blockhash", "block_hash", "txhash", "tx_hash", "transactionhash", "transaction_hash")

    override val priority: Int = 24
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, hashPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val hash = "0x" + generateHexString(64) // SHA-256 hash
        return "\"$hash\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateHexString(length: Int): String {
        val hexChars = "0123456789abcdef"
        return (1..length).map { hexChars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}