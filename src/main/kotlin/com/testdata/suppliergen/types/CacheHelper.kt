package com.testdata.suppliergen.types

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType
import java.util.concurrent.ConcurrentHashMap

/**
 * Helper class for caching TypeHandler resolutions and preventing recursion
 */
class CacheHelper {

    // Thread-safe cache for resolved handlers
    private val handlerCache = ConcurrentHashMap<String, TypeHandler>()

    // Thread-local stack to track current resolution chain and prevent recursion
    private val resolutionStack = ThreadLocal.withInitial { mutableSetOf<String>() }

    /**
     * Get a cached handler if available
     */
    fun getCachedHandler(fqName: String?, psiType: PsiType?): TypeHandler? {
        val cacheKey = createCacheKey(fqName, psiType)
        return handlerCache[cacheKey]
    }

    /**
     * Cache a resolved handler
     */
    fun cacheHandler(fqName: String?, psiType: PsiType?, handler: TypeHandler) {
        val cacheKey = createCacheKey(fqName, psiType)
        handlerCache[cacheKey] = handler
    }

    /**
     * Check if the current type is being resolved (recursion detection)
     */
    fun isCurrentlyResolving(fqName: String?, psiType: PsiType?): Boolean {
        val cacheKey = createCacheKey(fqName, psiType)
        return cacheKey in resolutionStack.get()
    }

    /**
     * Add type to resolution stack (call at start of resolution)
     */
    fun startResolving(fqName: String?, psiType: PsiType?) {
        val cacheKey = createCacheKey(fqName, psiType)
        resolutionStack.get().add(cacheKey)
    }

    /**
     * Remove type from resolution stack (call at end of resolution)
     */
    fun finishResolving(fqName: String?, psiType: PsiType?) {
        val cacheKey = createCacheKey(fqName, psiType)
        resolutionStack.get().remove(cacheKey)
    }

    /**
     * Get current resolution stack for debugging
     */
    fun getCurrentResolutionStack(): Set<String> {
        return resolutionStack.get().toSet()
    }

    /**
     * Creates a cache key from fqName and psiType information
     */
    private fun createCacheKey(fqName: String?, psiType: PsiType?): String {
        return when {
            fqName != null && psiType != null -> "${fqName}|${psiType.canonicalText}"
            fqName != null -> fqName
            psiType != null -> psiType.canonicalText
            else -> "unknown_${System.identityHashCode(psiType)}"
        }
    }

    /**
     * Clear the handler cache - useful for testing or when types change
     */
    fun clearCache() {
        handlerCache.clear()
    }

    /**
     * Clear current thread's resolution stack - useful for cleanup
     */
    fun clearResolutionStack() {
        resolutionStack.get().clear()
    }

    /**
     * Get cache statistics for monitoring/debugging
     */
    fun getCacheStats(): CacheStats {
        return CacheStats(
            cacheSize = handlerCache.size,
            cachedTypes = handlerCache.keys.toList(),
            currentResolutionStack = getCurrentResolutionStack()
        )
    }

    /**
     * Execute a block with proper recursion tracking
     */
    inline fun <T> withRecursionTracking(
        fqName: String?,
        psiType: PsiType?,
        onRecursion: () -> T,
        block: () -> T
    ): T {
        if (isCurrentlyResolving(fqName, psiType)) {
            return onRecursion()
        }

        startResolving(fqName, psiType)
        try {
            return block()
        } finally {
            finishResolving(fqName, psiType)
        }
    }
}

/**
 * Data class for cache statistics
 */
data class CacheStats(
    val cacheSize: Int,
    val cachedTypes: List<String>,
    val currentResolutionStack: Set<String>
)