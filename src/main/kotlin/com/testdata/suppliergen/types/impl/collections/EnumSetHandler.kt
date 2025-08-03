package com.testdata.suppliergen.types.impl.collections

import com.testdata.suppliergen.types.helpers.EnumHelper.getEnumConstantsFromClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil

object EnumSetHandler : CommonCollectionHandler() {

    override val supportedTypes: Set<String> = setOf("java.util.EnumSet", "EnumSet")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val psiClass = resolvePsiClass(fqName, psiType)
        val genericTypes = (psiType as? PsiClassType)?.parameters
        if (genericTypes.isNullOrEmpty()) {
            return "java.util.EnumSet.noneOf(null)"
        }
        if (genericTypes.size != 1) {
            println("EnumSetHandler: Expected one generic type, got ${genericTypes.size}")
            return "java.util.EnumSet.noneOf(null)"
        }
        val generics = (psiType as? PsiClassType)?.parameters
        val enumType = generics?.firstOrNull()
        val enumPsiClas = enumType?.let { PsiUtil.resolveClassInClassTypeOnly(it) }

        val constants = getEnumConstantsFromClass(enumPsiClas)
        // choose random constant from enum
        return if (constants.isNotEmpty()) {
            val index = (0 until constants.size).random()
            "java.util.EnumSet.of(${constants.getOrNull(index) ?: constants.first()})"
        } else {
            "java.util.EnumSet.noneOf(${enumPsiClas?.qualifiedName ?: "null"})"
        }
    }

    override val staticExtraImports: Set<String> get() = setOf("java.util.EnumSet")
}
