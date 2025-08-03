package com.testdata.suppliergen.v2

import com.testdata.suppliergen.generator.GenerationContext
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager

// 10. Destination Directory Resolver
class DestinationDirectoryResolver {
    private val packageHandler = PackageDirectoryHandler()

    fun resolveDestination(ctx: GenerationContext, file: PsiJavaFile): PsiDirectory {
        val targetDir = ctx.targetDir ?: throw IllegalStateException("Target directory is null")
        if (ctx.generateInSinglePackage) return targetDir

        val testSourceRoot = ProjectRootManager.getInstance(ctx.project).fileIndex
            .getSourceRootForFile(targetDir.virtualFile)
            ?: throw IllegalStateException("Unable to determine source root")

        val psiManager = PsiManager.getInstance(ctx.project)
        val testRootDir = psiManager.findDirectory(testSourceRoot)
            ?: throw IllegalStateException("Unable to find PSI directory for source root")

        return packageHandler.createOrFindDirectoryForPackage(
            ctx.project, testRootDir, file.packageName
        )
    }
}