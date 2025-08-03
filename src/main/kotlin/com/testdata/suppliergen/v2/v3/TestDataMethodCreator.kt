package com.testdata.suppliergen.v2.v3

import com.intellij.psi.PsiJavaFile;

import com.testdata.suppliergen.SupplierOptionsDialog;
import com.testdata.suppliergen.factory.FieldModelFactory;
import com.testdata.suppliergen.generator.sections.*;
import com.testdata.suppliergen.model.GenerationResult;
import com.testdata.suppliergen.model.SupplierClassModel;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

// 2. TestDataMethodCreator - Fixed to create correct supplier methods
class TestDataMethodCreator {
    fun getMethodName(supplierFile: PsiJavaFile): String {
        val supplierSimpleClassName = supplierFile.name.removeSuffix(".java")
        val classNameToUse = supplierSimpleClassName.removeSuffix("Supplier")
        return classNameToUse + "ConfiguredBuilder"
    }

    fun createSupplierMethod(testDataClass: PsiClass, supplierFile: PsiJavaFile): PsiMethod {
        val project = testDataClass.project
        val psiFacade = JavaPsiFacade.getInstance(project)
        val elementFactory = psiFacade.elementFactory

        val supplierClass = supplierFile.classes.firstOrNull()
        val supplierClassName = supplierClass?.qualifiedName ?: return createFallbackMethod(testDataClass, supplierFile)
        val methodName = getMethodName(supplierFile)
        val supplierSimpleClassName = supplierFile.name.removeSuffix(".java")
        val innerBuilder = supplierSimpleClassName + "Builder"

        val methodText = """
            public static $supplierClassName.$innerBuilder $methodName() {
                return $supplierClassName.configuredBuilder();
            }
        """.trimIndent()

        return elementFactory.createMethodFromText(methodText, testDataClass)
    }

    private fun createFallbackMethod(testDataClass: PsiClass, supplierFile: PsiJavaFile): PsiMethod {
        val project = testDataClass.project
        val elementFactory = JavaPsiFacade.getInstance(project).elementFactory
        val methodName = getMethodName(supplierFile)

        val methodText = """
            public static Object $methodName() {
                // TODO: Implement supplier method
                return null;
            }
        """.trimIndent()

        return elementFactory.createMethodFromText(methodText, testDataClass)
    }
}
