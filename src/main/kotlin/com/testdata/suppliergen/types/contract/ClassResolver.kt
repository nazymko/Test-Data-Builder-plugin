package com.testdata.suppliergen.types.contract

import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope

object ClassResolver {
    fun resolvePsiClass(fqName: String?, psiType: PsiType?): PsiClass? {
        // Prefer psiType if available
        val fromPsiType = (psiType as? PsiClassType)?.resolve()
        if (fromPsiType != null) return fromPsiType

        // Otherwise try to resolve by fqName via JavaPsiFacade (requires Project context)
        if (fqName == null) return null

        val project = findAnyProject() ?: return null
        return JavaPsiFacade.getInstance(project).findClass(fqName, GlobalSearchScope.allScope(project))
    }

    fun findAnyProject(): com.intellij.openapi.project.Project? {
        return ProjectManager.getInstance().openProjects.firstOrNull()
    }
}