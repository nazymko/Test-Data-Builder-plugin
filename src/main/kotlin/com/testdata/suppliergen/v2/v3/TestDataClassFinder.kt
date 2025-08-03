package com.testdata.suppliergen.v2.v3

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.jps.model.java.JavaSourceRootType

// Finds test data classes in the project
class TestDataClassFinder {

    fun findTestDataClass(project: Project, fqClassName: String): PsiClass? {
        // Method 1: Try JavaPsiFacade first (fastest)
        val psiFacade = JavaPsiFacade.getInstance(project)
        val searchScope = GlobalSearchScope.projectScope(project)

        val psiClass = psiFacade.findClass(fqClassName, searchScope)
        if (psiClass != null && isInTestSources(project, psiClass)) {
            return psiClass
        }

        // Method 2: Try with test scope only
        val testScope = createTestScope(project)
        if (testScope != null) {
            val testClass = psiFacade.findClass(fqClassName, testScope)
            if (testClass != null) return testClass
        }

        // Method 3: Fallback to file system search (original approach)
        return fallbackSearch(project, fqClassName)
    }

    private fun isInTestSources(project: Project, psiClass: PsiClass): Boolean {
        val virtualFile = psiClass.containingFile?.virtualFile ?: return false
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
        return fileIndex.isInTestSourceContent(virtualFile)
    }

    private fun createTestScope(project: Project): GlobalSearchScope? {
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
        val testRoots = ProjectRootManager.getInstance(project)
            .contentSourceRoots
            .filter { fileIndex.isUnderSourceRootOfType(it, setOf(JavaSourceRootType.TEST_SOURCE)) }

        if (testRoots.isEmpty()) return null

        return GlobalSearchScope.filesScope(project, testRoots.flatMap { root ->
            collectJavaFiles(root)
        })
    }

    private fun collectJavaFiles(root: VirtualFile): List<VirtualFile> {
        val javaFiles = mutableListOf<VirtualFile>()
        VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Void>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (!file.isDirectory && file.extension == "java") {
                    javaFiles.add(file)
                }
                return true
            }
        })
        return javaFiles
    }

    private fun fallbackSearch(project: Project, fqClassName: String): PsiClass? {
        val psiManager = PsiManager.getInstance(project)
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex

        val testRoots = ProjectRootManager.getInstance(project)
            .contentSourceRoots
            .filter { fileIndex.isUnderSourceRootOfType(it, setOf(JavaSourceRootType.TEST_SOURCE)) }

        for (root in testRoots) {
            val foundClass = searchInRoot(root, fqClassName, psiManager)
            if (foundClass != null) return foundClass
        }
        return null
    }

    private fun searchInRoot(root: VirtualFile, fqClassName: String, psiManager: PsiManager): PsiClass? {
        var found: PsiClass? = null
        VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Void>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (!file.isDirectory && file.extension == "java") {
                    found = findClassInFile(file, fqClassName, psiManager)
                    if (found != null) return false
                }
                return true
            }
        })
        return found
    }

    private fun findClassInFile(file: VirtualFile, fqClassName: String, psiManager: PsiManager): PsiClass? {
        val psiFile = psiManager.findFile(file) as? PsiJavaFile ?: return null
        return psiFile.classes.find { it.qualifiedName == fqClassName }
    }
}
