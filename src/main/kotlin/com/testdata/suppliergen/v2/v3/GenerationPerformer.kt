package com.testdata.suppliergen.v2.v3

import com.testdata.suppliergen.v2.*
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory

// 3. GenerationPerformer - Minor fix to pass correct file to TestDataMethodAdder
class GenerationPerformer {
    private val contextBuilder = GenerationContextBuilder()
    private val fileCoordinator = FileGenerationCoordinator()
    private val fileProcessor = GeneratedFileProcessor()
    private val navigator = FileNavigator()
    private val testDataMethodAdder = TestDataMethodAdder()

    fun perform(
        context: ActionContext,
        targetClass: PsiClass,
        options: DialogOptions,
        targetDir: PsiDirectory
    ) {
        val ctx = contextBuilder.buildContext(context.project, targetDir, options)
        val result = fileCoordinator.generateFiles(ctx, context.psiFile, targetClass)
        val processedFiles = fileProcessor.processAllFiles(context, ctx, result)

        processedFiles.lastOrNull()?.let { lastFile ->
            navigator.navigateToClass(context.project, lastFile)
            // Pass the supplier file (lastFile) to add methods to TestData class
            testDataMethodAdder.addMethodIfNeeded(context.project, ctx.testData, lastFile)
        }
    }
}
