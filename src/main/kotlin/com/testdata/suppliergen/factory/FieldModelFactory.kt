package com.testdata.suppliergen.factory

import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.types.TypeHandlerRegistry
import com.testdata.suppliergen.types.contract.ClassResolver
import com.testdata.suppliergen.types.impl.collections.CollectionHandler
import com.testdata.suppliergen.types.impl.OptionalHandler
import com.testdata.suppliergen.types.impl.maps.CommonMapHandler
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiField
import com.intellij.psi.PsiType

object FieldModelFactory {
    fun from(field: PsiField): FieldModel {
        val psiType = field.type
        val fqName = FqNameTypeResolver.canonicalClassNameToFqName(psiType)
        val handler = TypeHandlerRegistry.resolve(fqName, psiType, "FieldModelFactory field type")

        val name = field.name
        val type = psiType.presentableText
        val defaultValue = handler.defaultValue(name, fqName, psiType)
        val randomizedValue = handler.randomizedValue(name, fqName, psiType)

        val cap = name.replaceFirstChar { it.uppercaseChar() }
        val getter = "${handler.getterPrefix(fqName, psiType)}$cap"
        val setter = "set$cap"

        var base = FieldModel(
            name = name,
            typeHandler = handler,
            type = type,
            fqType = fqName,
            defaultValue = defaultValue,
            randomizedValue = randomizedValue,
            getter = getter,
            setter = setter,
            isKnown = handler.isKnown,
            extraImports = handler.staticExtraImports + handler.customImports(fqName, psiType),
            psiType = psiType,
            psiClass = ClassResolver.resolvePsiClass(fqName, psiType)
        )

        if (handler is CollectionHandler) {
            val info = handler.inspect(psiType)
            base = base.copy(
                isCollection = true,
                collectionRawType = info.rawType,
                elementPresentableType = info.elementPresentableType,
                elementFqType = info.elementFqType,
                elementIsKnown = info.elementIsKnown,
                isKnown = true
            )
        }

        if (handler is OptionalHandler || fqName?.startsWith("java.util.Optional") == true) {
            // Extract inner type fqName and presentable type from psiType (more precise than string manipulation)
            val innerPsiType = (psiType as? PsiClassType)?.parameters?.firstOrNull()
            val innerFqName = innerPsiType?.canonicalText ?: "java.lang.Object"
            val innerPresentable = innerPsiType?.presentableText ?: "Object"

            // Resolve inner handler to check if known
            val innerHandler =
                TypeHandlerRegistry.resolve(innerFqName, innerPsiType ?: psiType, "Optional Handler -> Inner value")
            val innerIsKnown = innerHandler.isKnown

            base = base.copy(
                isOptional = true,
                optionalInnerType = innerPresentable,
                optionalInnerIsKnown = innerIsKnown,
                isKnown = true,
                optionalInnerFqType = innerFqName,
            )
        }

        if (handler is CommonMapHandler) {
            val (mapType, keyType, valueType) = handler.extractTypes(fqName)
            val (keyPsiType, valuePsiType, mapPsiClass) = handler.extractMapTypes(psiType)

            val keyIsKnown = TypeHandlerRegistry.resolve(keyType, keyPsiType, "CommonMapHandler key resolver").isKnown
            val valueIsKnown =
                TypeHandlerRegistry.resolve(valueType, valuePsiType, "CommonMapHandler value resolver").isKnown

            base = base.copy(
                isMap = true,
                mapRawType = mapType,
                keyType = keyType,
                valueType = valueType,
                keyIsKnown = keyIsKnown,
                valueIsKnown = valueIsKnown,
                isKnown = true,
                mapPsiType = mapPsiClass,
                keyPsiType = keyPsiType,
                valuePsiType = valuePsiType
            )
        }

        return base
    }

}

