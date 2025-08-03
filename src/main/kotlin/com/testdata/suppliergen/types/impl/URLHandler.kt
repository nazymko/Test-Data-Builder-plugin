package com.testdata.suppliergen.types.impl

import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.model.FieldModel
import com.testdata.suppliergen.types.contract.HelperMethodMetadata
import com.testdata.suppliergen.types.contract.TypeHandler
import com.intellij.psi.PsiType

object URLHandler : TypeHandler {


    override val supportedTypes: Set<String> get() = setOf("java.net.URL", "URL")

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return "createUrl(\"https://testdata.com\")"
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) =
        defaultValue(fieldName, fqName, psiType)

    override val staticExtraImports: Set<String>
        get() = setOf(
            "java.net.URL",
            "java.net.URI",
            "java.net.MalformedURLException",
            "java.net.URISyntaxException"
        )

    override fun helperMethod(model: FieldModel, ctx: GenerationContext): HelperMethodMetadata? {
        val body = """
            private static URL createUrl(String url) {
                try {
                    return new URI(url).toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        """.trimIndent()
        return HelperMethodMetadata(body, "createUrl", emptyList())
    }
}
