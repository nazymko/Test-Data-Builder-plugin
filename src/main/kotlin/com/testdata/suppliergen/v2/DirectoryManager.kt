package com.testdata.suppliergen.v2

import com.testdata.suppliergen.files.StructureCreator
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile

// 6. Directory Manager
class DirectoryManager {
    private val structureCreator = StructureCreator()
    
    fun getTargetDirectory(psiFile: PsiJavaFile, generateInTest: Boolean): PsiDirectory? {
        return structureCreator.findOrCreateTargetDirectory(psiFile, generateInTest)
    }
}