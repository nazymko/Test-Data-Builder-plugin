package com.testdata.suppliergen.v2

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

// 8. Package Directory Handler
class PackageDirectoryHandler {
    fun createOrFindDirectoryForPackage(
        project: Project, baseDir: PsiDirectory, targetPackage: String
    ): PsiDirectory {
        val packagePath = targetPackage.replace('.', '/')
        var currentDir = baseDir

        for (part in packagePath.split("/")) {
            currentDir = currentDir.findSubdirectory(part)
                ?: currentDir.createSubdirectory(part)
        }
        return currentDir
    }
}