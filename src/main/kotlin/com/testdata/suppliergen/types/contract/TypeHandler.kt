package com.testdata.suppliergen.types.contract

import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.types.helpers.GenericSupportHelper
import com.testdata.suppliergen.types.impl.UnknownPojoHandler
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType

interface TypeHandler {
    fun supports(fqName: String?, psiType: PsiType?): Boolean =
        GenericSupportHelper.matchesAny(fqName, psiType, supportedTypes)

    val supportedTypes: Set<String> get() = emptySet()

    /** Expression for default value used in configuredBuilder() */
    fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String

     fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String =
        UnknownPojoHandler.defaultValue(fieldName, fqName, psiType)

    /** Whether we consider it “known” (no recursive generation needed) */
    val isKnown: Boolean get() = true

    /** Extra imports this handler needs (optional if you rely on shorten refs) */
    val staticExtraImports: Set<String> get() = emptySet()

    /** "get" or "is" (override for booleans if you want) */
    fun getterPrefix(fqName: String?, psiType: PsiType): String = "get"

    fun helperMethod(model: FieldModel, ctx: GenerationContext): HelperMethodMetadata? = null

    fun resolvePsiClass(fqName: String?, psiType: PsiType?): PsiClass? = ClassResolver.resolvePsiClass(fqName, psiType)

    fun customImports(fqName: String, psiType: PsiType) = emptySet<String>()

}