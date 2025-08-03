package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.impl.collections.helpers.BaseCollectionHandler
import com.intellij.psi.PsiType

object ConcurrentCollectionHandler : BaseCollectionHandler() {
    
    override val typeToFactoryMappings = mapOf(
        "java.util.concurrent.BlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "BlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "java.util.concurrent.LinkedBlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "LinkedBlockingQueue" to "java.util.concurrent.LinkedBlockingQueue",
        "java.util.concurrent.ArrayBlockingQueue" to "java.util.concurrent.ArrayBlockingQueue",
        "ArrayBlockingQueue" to "java.util.concurrent.ArrayBlockingQueue",
        "java.util.concurrent.PriorityBlockingQueue" to "java.util.concurrent.PriorityBlockingQueue",
        "PriorityBlockingQueue" to "java.util.concurrent.PriorityBlockingQueue",
        "java.util.concurrent.LinkedBlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "LinkedBlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "java.util.concurrent.BlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "BlockingDeque" to "java.util.concurrent.LinkedBlockingDeque",
        "java.util.concurrent.ConcurrentHashMap" to "java.util.concurrent.ConcurrentHashMap",
        "ConcurrentHashMap" to "java.util.concurrent.ConcurrentHashMap",
        "java.util.concurrent.ConcurrentLinkedQueue" to "java.util.concurrent.ConcurrentLinkedQueue",
        "ConcurrentLinkedQueue" to "java.util.concurrent.ConcurrentLinkedQueue",
        "java.util.concurrent.ConcurrentSkipListSet" to "java.util.concurrent.ConcurrentSkipListSet",
        "ConcurrentSkipListSet" to "java.util.concurrent.ConcurrentSkipListSet",
        "java.util.concurrent.ConcurrentSkipListMap" to "java.util.concurrent.ConcurrentSkipListMap",
        "ConcurrentSkipListMap" to "java.util.concurrent.ConcurrentSkipListMap"
    )
    
    override fun hasSpecialHandling(factoryType: String): Boolean =
        factoryType.startsWith("java.util.concurrent.ArrayBlockingQueue")
    
    override fun handleSpecialType(factoryType: String, psiType: PsiType, fieldName: String): String =
        when {
            factoryType.startsWith("java.util.concurrent.ArrayBlockingQueue") -> 
                "new java.util.concurrent.ArrayBlockingQueue<>(16)"
            else -> super.handleSpecialType(factoryType, psiType, fieldName)
        }
    
    override fun getDefaultFallback(): String = "new java.util.concurrent.ConcurrentHashMap<>()"
}