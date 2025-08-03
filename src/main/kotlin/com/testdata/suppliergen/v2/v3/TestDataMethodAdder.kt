package com.testdata.suppliergen.v2.v3

import com.testdata.suppliergen.SupplierOptionsDialog
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile

// Updated TestDataMethodAdder with proper CreateNew handling
class TestDataMethodAdder {
    private val testDataClassFinder = TestDataClassFinder()
    private val testDataClassCreator = TestDataClassCreator()
    private val methodCreator = TestDataMethodCreator()
    private val methodChecker = TestDataMethodChecker()

    fun addMethodIfNeeded(
        project: Project,
        testData: SupplierOptionsDialog.TestDataClassSelection?,
        supplierFile: PsiJavaFile?
    ) {
        if (testData == null || supplierFile == null) return

        when (testData) {
            is SupplierOptionsDialog.TestDataClassSelection.UseExisting -> {
                handleExistingTestDataClass(project, testData, supplierFile)
            }
            is SupplierOptionsDialog.TestDataClassSelection.CreateNew -> {
                handleNewTestDataClass(project, testData, supplierFile)
            }
        }
    }

    private fun handleExistingTestDataClass(
        project: Project,
        testData: SupplierOptionsDialog.TestDataClassSelection.UseExisting,
        supplierFile: PsiJavaFile
    ) {
        val testDataClass = testDataClassFinder.findTestDataClass(project, testData.fqClassName)
        if (testDataClass != null) {
            addMethodToTestDataClass(project, testDataClass, supplierFile)
        } else {
            println("TestData class '${testData.fqClassName}' not found under test sources.")
        }
    }

    private fun handleNewTestDataClass(
        project: Project,
        testData: SupplierOptionsDialog.TestDataClassSelection.CreateNew,
        supplierFile: PsiJavaFile
    ) {
        // First, try to find existing TestData class
        var testDataClass = testDataClassFinder.findTestDataClass(project, testData.className)

        // If not found, create it
        if (testDataClass == null) {
            testDataClass = testDataClassCreator.createTestDataClass(project, testData, supplierFile)
        }

        // Add supplier method to the TestData class
        if (testDataClass != null) {
            addMethodToTestDataClass(project, testDataClass, supplierFile)
        } else {
            println("Failed to create or find TestData class '${testData.className}'")
        }
    }

    private fun addMethodToTestDataClass(project: Project, testDataClass: PsiClass, supplierFile: PsiJavaFile) {
        val methodName = methodCreator.getMethodName(supplierFile)

        if (!methodChecker.hasInitializerMethod(testDataClass, methodName)) {
            val method = methodCreator.createSupplierMethod(testDataClass, supplierFile)
            WriteCommandAction.runWriteCommandAction(project) {
                testDataClass.add(method)
            }
        }
    }
}