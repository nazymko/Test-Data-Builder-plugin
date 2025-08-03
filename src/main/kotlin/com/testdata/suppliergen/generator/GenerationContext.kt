package com.testdata.suppliergen.generator

import com.testdata.suppliergen.SupplierOptionsDialog.TestDataClassSelection
import com.testdata.suppliergen.model.InstantiationMode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

data class GenerationContext(
    val project: Project,
    val targetDir: PsiDirectory?,
    val maxDepth: Int = 5,
    val visited: MutableSet<String> = mutableSetOf(), // fqcn to avoid cycles
    val instantiationMode: InstantiationMode,
    val generateInSinglePackage: Boolean = false,
    val testData: TestDataClassSelection?,
)
