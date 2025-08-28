package com.testdata.suppliergen.v2

import com.testdata.suppliergen.generator.DirectoryValidator
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile

// 11. File Processor
class FileProcessor {
    private val logger = Logger.getInstance(FileProcessor::class.java)

    fun processFile(project: Project, destinationDir: PsiDirectory, file: PsiJavaFile): PsiJavaFile {
        // Validate directory is writable before attempting to write
        if (!DirectoryValidator.isWritableDirectory(destinationDir)) {
            val error = "Cannot write to read-only directory: ${destinationDir.virtualFile.path}"
            logger.error(error)
            throw IllegalStateException(error)
        }

        logger.debug("Processing file ${file.name} in directory: ${destinationDir.virtualFile.path}")
        
        try {
            val existing = destinationDir.findFile(file.name)
            existing?.delete()
            
            return destinationDir.add(file) as PsiJavaFile
        } catch (e: Exception) {
            val error = "Failed to write file ${file.name} to ${destinationDir.virtualFile.path}: ${e.message}"
            logger.error(error, e)
            throw IllegalStateException(error, e)
        }
    }
}