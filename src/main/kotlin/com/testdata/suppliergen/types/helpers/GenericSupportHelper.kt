package com.testdata.suppliergen.types.helpers

import com.intellij.psi.PsiType

object GenericSupportHelper {

    fun matchesAny(fqName: String?, psiType: PsiType?, possibleNames: Set<String>): Boolean {
//        println("GenericSupportHelper fqName = ${fqName}")
        fqName?.let {
            if (it in possibleNames) return true
        }
        psiType?.let {
            if (possibleNames.any { name -> psiType.equalsToText(name) }) return true
        }
        return false
    }
}