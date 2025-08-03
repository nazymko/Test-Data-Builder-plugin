package com.testdata.suppliergen.files

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import org.jetbrains.jps.model.java.JavaSourceRootType
class StructureCreator {
    fun findOrCreateTargetDirectory(
        psiFile: PsiFile, generateInTest: Boolean
    ): PsiDirectory? {
        val project: Project = psiFile.project
        val psiManager = PsiManager.getInstance(project)

        val packageName = (psiFile as? PsiJavaFile)?.packageName ?: return null
        val packageParts = packageName.split('.')

        val sourceRootType = if (generateInTest) JavaSourceRootType.TEST_SOURCE else JavaSourceRootType.SOURCE

        // 1) Try module source roots first
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            val roots = ModuleRootManager.getInstance(module).getSourceRoots(sourceRootType)
            for (root in roots) {
                val baseDir = psiManager.findDirectory(root) ?: continue
                return createPackageDirs(baseDir, packageParts)
            }
        }

        // 2) Fallback: create under project base directory
        val projectBaseDir = ProjectRootManager.getInstance(project).contentRoots.firstOrNull() ?: return null
        val basePsiDir = psiManager.findDirectory(projectBaseDir) ?: return null

        val fallbackPath = if (generateInTest) listOf("src", "test", "java") else listOf("src", "main", "java")

        val fallbackBaseDir = createPackageDirs(basePsiDir, fallbackPath)
        return createPackageDirs(fallbackBaseDir, packageParts)
    }

    private fun createPackageDirs(baseDir: PsiDirectory, packageParts: List<String>): PsiDirectory {
        var currentDir = baseDir
        for (part in packageParts) {
            currentDir = com.intellij.openapi.application.WriteAction.compute<PsiDirectory, Throwable> {
                currentDir.findSubdirectory(part) ?: currentDir.createSubdirectory(part)
            }
        }
        return currentDir
    }
}