package com.testdata.suppliergen.v2.v3

import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.model.GenerationResult
import com.testdata.suppliergen.v2.ActionContext
import com.testdata.suppliergen.v2.*
import com.intellij.psi.PsiJavaFile

// Processes all generated files
class GeneratedFileProcessor {
    private val destinationResolver = DestinationDirectoryResolver()
    private val fileProcessor = FileProcessor()
    private val formatter = CodeStyleFormatter()

    fun processAllFiles(
        context: ActionContext,
        ctx: GenerationContext,
        result: GenerationResult
    ): List<PsiJavaFile> {
        val allFiles = listOf(result.root) + result.dependencies
        val processedFiles = mutableListOf<PsiJavaFile>()

        allFiles.reversed().distinctBy { it.packageName + it.name }.forEach { file ->
            val destinationDir = destinationResolver.resolveDestination(ctx, file)
            val addedFile = fileProcessor.processFile(context.project, destinationDir, file)
            formatter.formatFile(context.project, addedFile, context.psiFile)
            processedFiles.add(addedFile)
        }
        
        return processedFiles
    }
}
