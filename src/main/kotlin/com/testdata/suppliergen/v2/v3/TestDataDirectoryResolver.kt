package com.testdata.suppliergen.v2.v3

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import org.jetbrains.jps.model.java.JavaSourceRootType

// Resolves where to create the TestData class
class TestDataDirectoryResolver {

    fun resolveTargetDirectory(project: Project, supplierFile: PsiJavaFile): PsiDirectory? {
        // Get the directory of the supplier file
        val supplierDirectory = supplierFile.containingDirectory
        if (supplierDirectory == null) {
            println("Could not get directory of supplier file")
            return null
        }

        // Check if supplier is already in test sources
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
        val supplierVirtualFile = supplierFile.virtualFile

        if (supplierVirtualFile != null && fileIndex.isInTestSourceContent(supplierVirtualFile)) {
            // Supplier is in test sources, use same directory
            return supplierDirectory
        }

        // Supplier is in main sources, find corresponding test directory
        return findCorrespondingTestDirectory(project, supplierDirectory)
    }

    private fun findCorrespondingTestDirectory(project: Project, sourceDirectory: PsiDirectory): PsiDirectory? {
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
        val packageName = JavaDirectoryService.getInstance().getPackage(sourceDirectory)?.qualifiedName ?: ""


        // Find test source roots
        val testRoots = ProjectRootManager.getInstance(project)
            .contentSourceRoots
            .filter { fileIndex.isUnderSourceRootOfType(it, setOf(JavaSourceRootType.TEST_SOURCE)) }

        for (testRoot in testRoots) {
            val testPsi = PsiManager.getInstance(project).findDirectory(testRoot)
            if (testPsi != null) {
                val testPackageDir = findOrCreatePackageDirectory(testPsi, packageName)
                if (testPackageDir != null) return testPackageDir
            }
        }

        println("Could not find or create test directory for package: $packageName")
        return null
    }

    private fun findOrCreatePackageDirectory(rootDir: PsiDirectory, packageName: String): PsiDirectory? {
        if (packageName.isEmpty()) return rootDir

        val packages = packageName.split(".")
        var currentDir = rootDir

        for (packagePart in packages) {
            var subDir = currentDir.findSubdirectory(packagePart)
            if (subDir == null) {
                try {
                    subDir = currentDir.createSubdirectory(packagePart)
                } catch (e: Exception) {
                    println("Failed to create directory: $packagePart")
                    return null
                }
            }
            currentDir = subDir
        }

        return currentDir
    }
}