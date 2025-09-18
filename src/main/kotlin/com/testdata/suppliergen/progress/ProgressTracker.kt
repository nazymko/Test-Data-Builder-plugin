package com.testdata.suppliergen.progress

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField

/**
 * Progress tracking system for supplier generation operations.
 * Provides field-level progress tracking with detailed phase information.
 */
class ProgressTracker {

    data class ProgressInfo(
        val totalFields: Int,
        val processedFields: Int = 0,
        val currentPhase: GenerationPhase = GenerationPhase.INITIALIZATION,
        val currentFieldName: String? = null,
        val estimatedTimeRemaining: Long = 0
    ) {
        val progressPercent: Double = if (totalFields > 0) (processedFields.toDouble() / totalFields * 100) else 0.0
        val isComplete: Boolean = processedFields >= totalFields
    }

    enum class GenerationPhase(val displayName: String, val weight: Double) {
        INITIALIZATION("Initializing generation...", 0.05),
        FIELD_ANALYSIS("Analyzing class fields...", 0.10),
        TYPE_RESOLUTION("Resolving field types...", 0.15),
        CODE_GENERATION("Generating supplier code...", 0.60),
        FILE_CREATION("Creating files...", 0.08),
        FINALIZATION("Finalizing generation...", 0.02)
    }

    private var startTime: Long = 0
    private var currentProgress = ProgressInfo(0)
    private val phaseTimings = mutableMapOf<GenerationPhase, Long>()

    fun initialize(totalFields: Int): ProgressInfo {
        startTime = System.currentTimeMillis()
        currentProgress = ProgressInfo(totalFields)
        return currentProgress
    }

    fun updatePhase(phase: GenerationPhase): ProgressInfo {
        phaseTimings[currentProgress.currentPhase] = System.currentTimeMillis()
        currentProgress = currentProgress.copy(currentPhase = phase)
        return currentProgress
    }

    fun incrementField(fieldName: String): ProgressInfo {
        val newProcessed = currentProgress.processedFields + 1
        val estimatedTime = calculateEstimatedTime(newProcessed)

        currentProgress = currentProgress.copy(
            processedFields = newProcessed,
            currentFieldName = fieldName,
            estimatedTimeRemaining = estimatedTime
        )
        return currentProgress
    }

    fun getCurrentProgress(): ProgressInfo = currentProgress

    private fun calculateEstimatedTime(processedFields: Int): Long {
        if (processedFields == 0 || startTime == 0L) return 0

        val elapsed = System.currentTimeMillis() - startTime
        val avgTimePerField = elapsed / processedFields
        val remainingFields = currentProgress.totalFields - processedFields

        return avgTimePerField * remainingFields
    }

    /**
     * Calculate total field count from target class including nested types
     */
    fun calculateTotalFields(targetClass: PsiClass): Int {
        val directFields = targetClass.fields.size
        val nestedFields = calculateNestedFields(targetClass)
        return directFields + nestedFields
    }

    private fun calculateNestedFields(psiClass: PsiClass): Int {
        var count = 0
        psiClass.fields.forEach { field ->
            // Count fields in nested custom types that will need suppliers
            if (isCustomType(field)) {
                count += estimateNestedFieldCount(field)
            }
        }
        return count
    }

    private fun isCustomType(field: PsiField): Boolean {
        val typeName = field.type.canonicalText
        return !typeName.startsWith("java.") &&
               !typeName.startsWith("kotlin.") &&
               !isPrimitiveType(typeName)
    }

    private fun isPrimitiveType(typeName: String): Boolean {
        return typeName in setOf("int", "long", "double", "float", "boolean", "char", "byte", "short")
    }

    private fun estimateNestedFieldCount(field: PsiField): Int {
        // Conservative estimate for nested types
        return 5 // Average number of fields in a nested class
    }
}