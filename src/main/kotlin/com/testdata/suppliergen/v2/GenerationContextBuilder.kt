package com.testdata.suppliergen.v2

import com.testdata.suppliergen.generator.GenerationContext
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

// 7. Generation Context Builder
class GenerationContextBuilder {
    fun buildContext(
        project: Project, targetDir: PsiDirectory, options: DialogOptions
    ): GenerationContext {
        return GenerationContext(
            project, targetDir, maxDepth = options.maxDepth,
            instantiationMode = options.instantiationMode,
            testData = options.testData,
            generateInSinglePackage = options.shouldGenerateInSinglePackage
        )
    }
}