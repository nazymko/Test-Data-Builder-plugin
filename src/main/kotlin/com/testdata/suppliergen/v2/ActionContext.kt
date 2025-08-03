package com.testdata.suppliergen.v2

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiJavaFile

data class ActionContext(val project: Project, val editor: Editor, val psiFile: PsiJavaFile)