package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

abstract class CommonCollectionHandler : TypeHandler {
override fun supports(fqName: String?, psiType: PsiType?): Boolean {
    if (fqName == null) return false

    // Extract raw type FQCN from fqName (strip generics)
    val rawFqName = fqName.substringBefore('<')

    // Also get simple name without package
    val simpleName = rawFqName.substringAfterLast('.')

    // Check if rawFqName or simpleName is in supportedTypes
    return supportedTypes.any { it == rawFqName || it == simpleName }
}
}