package com.testdata.suppliergen.model

import com.intellij.psi.PsiJavaFile

data class GenerationResult(
    val root: PsiJavaFile,
    val dependencies: List<PsiJavaFile>
)
