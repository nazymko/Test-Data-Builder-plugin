package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

// Usage testdata - registry that combines multiple handlers
object CollectionHandlerRegistry {
    val handlers = listOf(
        JavaCollectionHandler,
        ConcurrentCollectionHandler,
        GuavaCollectionHandler,
        ArrayDequeHandler,
        BitSetHandler,
        BlockingQueueHandler,
        EnumSetHandler,
        HashtableHandler,
        LinkedBlockingDequeHandler,
        LinkedBlockingQueueHandler,
        NavigableMapHandler,
        NavigableSetHandler,
        PriorityQueueHandler,
        PropertiesHandler,
        StackHandler,
        SynchronizedListHandler,
        SynchronizedMapHandler,
        SynchronizedSetHandler,
        TreeSetHandler,
        VectorHandler,

        )

}