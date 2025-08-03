package com.testdata.suppliergen.v2

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiJavaFile

// 13. File Navigator
class FileNavigator {
    fun navigateToClass(project: Project, psiFile: PsiJavaFile) {
        val generatedClass = psiFile.classes.firstOrNull() ?: return
        
        ApplicationManager.getApplication().invokeLater {
            OpenFileDescriptor(project, psiFile.virtualFile, generatedClass.textOffset)
                .navigate(true)
        }
    }
}
