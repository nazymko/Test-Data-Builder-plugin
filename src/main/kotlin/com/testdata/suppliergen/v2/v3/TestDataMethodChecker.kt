package com.testdata.suppliergen.v2.v3

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiModifier

// Checks if test data methods already exist
class TestDataMethodChecker {
    fun hasInitializerMethod(psiClass: PsiClass, methodName: String): Boolean {
        return psiClass.methods.any {
            it.name == methodName &&
            it.parameterList.parametersCount == 0 &&
            it.hasModifierProperty(PsiModifier.STATIC)
        }
    }
}