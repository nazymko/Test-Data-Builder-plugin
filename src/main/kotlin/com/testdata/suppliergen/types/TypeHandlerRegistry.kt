package com.testdata.suppliergen.types

import com.testdata.suppliergen.types.contract.TypeHandler
import com.testdata.suppliergen.types.impl.*
import com.testdata.suppliergen.types.impl.collections.*
import com.testdata.suppliergen.types.impl.maps.*
import com.intellij.psi.PsiType

object TypeHandlerRegistry {
    private val handlers: List<TypeHandler> = listOf(
        StringHandler,
        BigDecimalHandler,
        BooleanHandler,
        ConcurrentHashMapHandler,
        EnumMapHandler,
        ListMultimapHandler,
        ConcurrentSkipListMapHandler,
        NavigableMapHandler,
        ConcurrentMapHandler,
        GuavaMapHandler,
        SynchronizedMapHandler,
        SortedMapHandler,
        HashMapHandler,
        CommonMapHandler(),
        IdentityHashMapHandler,
        LinkedHashMapHandler,
        TreeMapHandler,
        HashtableHandler,
        WeakHashMapHandler,
        BooleanWrapperHandler,
        DateHandler,
        DoubleHandler,
        DurationHandler,
        InstantHandler,
        IntHandler,
        LocalDateHandler,
        LocalDateTimeHandler,
        LongHandler,
        ObjectHandler,
        OffsetDateTimeHandler,
        OptionalHandler,
        URIHandler,
        URLHandler,
        UUIDHandler,
        ZoneIdHandler,
        EnumHandler,
        ArrayDequeHandler,
        BitSetHandler,
        BlockingQueueHandler,
        EnumSetHandler,
        LinkedBlockingDequeHandler,
        GuavaCollectionHandler,
        NavigableSetHandler,
        PriorityQueueHandler,
        PropertiesHandler,
        StackHandler,
        SynchronizedListHandler,
        SynchronizedSetHandler,
        TreeSetHandler,
        VectorHandler,
        LinkedBlockingQueueHandler,
        CollectionHandler,
    ) + CollectionHandlerRegistry.handlers

    // Cache helper instance
    private val cacheHelper = CacheHelper()

    fun resolve(fqName: String?, psiType: PsiType?, desc: String?): TypeHandler {
        val result: TypeHandler

        // Check cache first
        val cachedHandler = cacheHelper.getCachedHandler(fqName, psiType)

        if (cachedHandler != null) {
            println("fqName = [${fqName}], psiType = [${psiType}] resolved from CACHE = [${cachedHandler}]. Desc = [${desc}]")
            result = cachedHandler
        } else {
            // Use cache helper's recursion tracking
            result = cacheHelper.withRecursionTracking(
                fqName = fqName,
                psiType = psiType,
                onRecursion = {
                    println("RECURSION DETECTED for fqName = [${fqName}], psiType = [${psiType}]. Stack = [${cacheHelper.getCurrentResolutionStack()}]. Desc = [${desc}]")
                    // Cache and return UnknownPojoHandler to break the cycle
                    val recursiveHandler = UnknownPojoHandler
                    cacheHelper.cacheHandler(fqName, psiType, recursiveHandler)
                    recursiveHandler
                }
            ) {
                // Resolve handler
                val handler = handlers.firstOrNull { it.supports(fqName, psiType) } ?: UnknownPojoHandler
                println("fqName = [${fqName}], psiType = [${psiType}] resolved into handler = [${handler}]. Desc = [${desc}]")

                // Cache the result
                cacheHelper.cacheHandler(fqName, psiType, handler)
                handler
            }
        }

        if (result is UnknownPojoHandler) {
            println("Using UnknownPojoHandler for fqName = [${fqName}], psiType = [${psiType}]. Desc = [${desc}]")
        }
        return result
    }

    /**
     * Clear the handler cache - useful for testing or when types change
     */
    fun clearCache() {
        cacheHelper.clearCache()
        println("TypeHandlerRegistry cache cleared")
    }

    /**
     * Clear current thread's resolution stack - useful for cleanup
     */
    fun clearResolutionStack() {
        cacheHelper.clearResolutionStack()
    }

    /**
     * Get cache statistics for monitoring/debugging
     */
    fun getCacheStats(): CacheStats {
        return cacheHelper.getCacheStats()
    }

    /**
     * Check if a type is currently being resolved (for debugging)
     */
    fun isCurrentlyResolving(fqName: String?, psiType: PsiType?): Boolean {
        return cacheHelper.isCurrentlyResolving(fqName, psiType)
    }
}