package com.testdata.suppliergen.v2

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile

// 11. File Processor
class FileProcessor {
    fun processFile(project: Project, destinationDir: PsiDirectory, file: PsiJavaFile): PsiJavaFile {
        val existing = destinationDir.findFile(file.name)
        existing?.delete()
        
        return destinationDir.add(file) as PsiJavaFile
    }
}