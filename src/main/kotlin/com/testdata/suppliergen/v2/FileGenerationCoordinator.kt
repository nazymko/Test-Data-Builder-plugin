package com.testdata.suppliergen.v2

import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.generator.SupplierGenerator
import com.testdata.suppliergen.model.GenerationResult
import com.testdata.suppliergen.progress.ProgressCallback
import com.intellij.psi.*


// 9. File Generation Coordinator
class FileGenerationCoordinator {
    fun generateFiles(
        ctx: GenerationContext,
        psiFile: PsiJavaFile,
        targetClass: PsiClass,
        progressCallback: ProgressCallback
    ): GenerationResult {
        val generator = SupplierGenerator(ctx.project)
        return generator.generateRecursively(ctx, psiFile, targetClass, progressCallback)
    }
}