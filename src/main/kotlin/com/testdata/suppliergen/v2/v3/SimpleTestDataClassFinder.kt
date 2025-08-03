package com.testdata.suppliergen.v2.v3

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope

// Alternative simpler approach - if the above is still too complex
class SimpleTestDataClassFinder {
    
    fun findTestDataClass(project: Project, fqClassName: String): PsiClass? {
        // Use IntelliJ's built-in class finder with project scope
        val psiFacade = JavaPsiFacade.getInstance(project)
        val projectScope = GlobalSearchScope.projectScope(project)
        
        val psiClass = psiFacade.findClass(fqClassName, projectScope)
        
        // Verify it's in test sources
        if (psiClass != null) {
            val virtualFile = psiClass.containingFile?.virtualFile
            if (virtualFile != null) {
                val fileIndex = ProjectRootManager.getInstance(project).fileIndex
                if (fileIndex.isInTestSourceContent(virtualFile)) {
                    return psiClass
                }
            }
        }
        
        return null
    }
}