package com.testdata.suppliergen.generator

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiDirectory

/**
 * Validates that directories are writable for supplier generation.
 */
object DirectoryValidator {
    private val logger = Logger.getInstance(DirectoryValidator::class.java)

    /**
     * Checks if a directory is safe for writing supplier files.
     * Only blocks actual library/JAR locations, not legitimate project directories.
     */
    fun isWritableDirectory(directory: PsiDirectory?): Boolean {
        if (directory == null) {
            logger.warn("Directory is null")
            return false
        }

        val virtualFile = directory.virtualFile
        
        if (!virtualFile.isWritable) {
            logger.info("Directory is not writable: ${virtualFile.path}")
            return false
        }

        // Check for library/JAR-specific read-only patterns only
        val path = virtualFile.path
        val libraryPatterns = listOf(
            ".jar!",           // JAR file contents
            ".class"           // Direct .class file locations
        )

        for (pattern in libraryPatterns) {
            if (path.contains(pattern)) {
                logger.info("Directory contains library pattern '$pattern': $path")
                return false
            }
        }

        return true
    }

    /**
     * Checks specifically for library class directories that should never be written to.
     */
    fun isLibraryDirectory(directory: PsiDirectory?): Boolean {
        if (directory == null) return false
        
        val path = directory.virtualFile.path
        val libraryPatterns = listOf(
            ".jar!",
            "/lib/",
            "\\lib\\",
            "/.gradle/caches/",
            "\\.gradle\\caches\\",
            "/build/classes/java/",
            "\\build\\classes\\java\\",
            "/target/classes/",
            "\\target\\classes\\"
        )

        return libraryPatterns.any { path.contains(it) }
    }

    /**
     * Gets a safe directory for writing, using fallback if primary is not writable.
     */
    fun getSafeDirectory(primaryDirectory: PsiDirectory?, fallbackDirectory: PsiDirectory?): PsiDirectory {
        return when {
            isWritableDirectory(primaryDirectory) -> {
                logger.debug("Using primary directory: ${primaryDirectory?.virtualFile?.path}")
                primaryDirectory!!
            }
            isWritableDirectory(fallbackDirectory) -> {
                logger.info("Primary directory not writable, using fallback: ${fallbackDirectory?.virtualFile?.path}")
                fallbackDirectory!!
            }
            else -> {
                error("No writable directory available. Primary: ${primaryDirectory?.virtualFile?.path}, Fallback: ${fallbackDirectory?.virtualFile?.path}")
            }
        }
    }
}