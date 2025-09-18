package com.testdata.suppliergen.types.impl.patterns

import java.util.concurrent.ConcurrentHashMap

/**
 * Caching system for compiled regex patterns to improve performance.
 * Patterns are compiled once and reused across multiple field name matches.
 */
object PatternCache {
    private val compiledPatterns = ConcurrentHashMap<String, Regex>()

    /**
     * Get a compiled regex pattern, creating and caching it if not already present.
     */
    fun getCompiledPattern(pattern: String): Regex {
        return compiledPatterns.getOrPut(pattern) {
            pattern.toRegex(RegexOption.IGNORE_CASE)
        }
    }

    /**
     * Clear all cached patterns. Useful for testing or memory management.
     */
    fun clearCache() {
        compiledPatterns.clear()
    }

    /**
     * Get cache statistics for monitoring.
     */
    fun getCacheStats(): PatternCacheStats {
        return PatternCacheStats(
            cacheSize = compiledPatterns.size,
            patterns = compiledPatterns.keys.toList()
        )
    }
}

/**
 * Statistics about the pattern cache.
 */
data class PatternCacheStats(
    val cacheSize: Int,
    val patterns: List<String>
)

/**
 * Pre-computed value pools for common data types to improve performance.
 */
object ValuePools {
    private const val POOL_SIZE = 1000

    // Pre-generated pools
    private val emailPool = lazy { generateEmailPool(POOL_SIZE) }
    private val phonePool = lazy { generatePhonePool(POOL_SIZE) }
    private val namePool = lazy { generateNamePool(POOL_SIZE) }
    private val addressPool = lazy { generateAddressPool(POOL_SIZE) }
    private val companyPool = lazy { generateCompanyPool(POOL_SIZE) }

    fun getRandomEmail(): String = emailPool.value.random()
    fun getRandomPhone(): String = phonePool.value.random()
    fun getRandomName(): String = namePool.value.random()
    fun getRandomAddress(): String = addressPool.value.random()
    fun getRandomCompany(): String = companyPool.value.random()

    private fun generateEmailPool(size: Int): List<String> {
        val firstNames = listOf("john", "jane", "mike", "sarah", "david", "lisa", "chris", "emma", "alex", "maria")
        val lastNames = listOf("smith", "johnson", "brown", "davis", "miller", "wilson", "moore", "taylor", "anderson", "jackson")
        val domains = listOf("example.com", "test.org", "sample.net", "demo.co.uk", "mock.io")

        return (1..size).map {
            val firstName = firstNames.random()
            val lastName = lastNames.random()
            val domain = domains.random()
            val separator = listOf(".", "_", "").random()
            "$firstName$separator$lastName@$domain"
        }
    }

    private fun generatePhonePool(size: Int): List<String> {
        return (1..size).map {
            val area = (200..999).random()
            val exchange = (200..999).random()
            val number = (1000..9999).random()
            "($area) $exchange-$number"
        }
    }

    private fun generateNamePool(size: Int): List<String> {
        val firstNames = listOf("Alexander", "Isabella", "William", "Sophia", "James", "Emma", "Benjamin", "Olivia", "Lucas", "Ava")
        val lastNames = listOf("Anderson", "Thompson", "Johnson", "Williams", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor")

        return (1..size).map {
            "${firstNames.random()} ${lastNames.random()}"
        }
    }

    private fun generateAddressPool(size: Int): List<String> {
        val streetNames = listOf("Main St", "Oak Ave", "First St", "Second St", "Park Rd", "Elm St", "Maple Ave", "Cedar Ln", "Pine St", "Washington Blvd")

        return (1..size).map {
            val number = (100..9999).random()
            val street = streetNames.random()
            "$number $street"
        }
    }

    private fun generateCompanyPool(size: Int): List<String> {
        val adjectives = listOf("Global", "United", "Advanced", "Dynamic", "Strategic", "Innovative", "Premier", "Elite", "Professional", "Integrated")
        val nouns = listOf("Systems", "Solutions", "Technologies", "Services", "Corporation", "Industries", "Enterprises", "Group", "Partners", "Associates")

        return (1..size).map {
            "${adjectives.random()} ${nouns.random()}"
        }
    }

    /**
     * Clear all value pools to free memory.
     */
    fun clearPools() {
        // Since we're using lazy initialization, we can't directly clear them
        // But we can provide a method for manual garbage collection
        System.gc()
    }

    /**
     * Get statistics about value pools.
     */
    fun getPoolStats(): ValuePoolStats {
        return ValuePoolStats(
            emailPoolSize = if (emailPool.isInitialized()) POOL_SIZE else 0,
            phonePoolSize = if (phonePool.isInitialized()) POOL_SIZE else 0,
            namePoolSize = if (namePool.isInitialized()) POOL_SIZE else 0,
            addressPoolSize = if (addressPool.isInitialized()) POOL_SIZE else 0,
            companyPoolSize = if (companyPool.isInitialized()) POOL_SIZE else 0
        )
    }
}

/**
 * Statistics about value pools.
 */
data class ValuePoolStats(
    val emailPoolSize: Int,
    val phonePoolSize: Int,
    val namePoolSize: Int,
    val addressPoolSize: Int,
    val companyPoolSize: Int
) {
    val totalInitializedPools = listOf(emailPoolSize, phonePoolSize, namePoolSize, addressPoolSize, companyPoolSize).count { it > 0 }
}