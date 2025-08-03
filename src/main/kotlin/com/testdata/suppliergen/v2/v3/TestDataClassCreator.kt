package com.testdata.suppliergen.v2.v3

import com.testdata.suppliergen.SupplierOptionsDialog
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.*

// New class to handle TestData class creation
class TestDataClassCreator {
    private val directoryResolver = TestDataDirectoryResolver()

    fun createTestDataClass(
        project: Project,
        testData: SupplierOptionsDialog.TestDataClassSelection.CreateNew,
        supplierFile: PsiJavaFile
    ): PsiClass? {
        return WriteCommandAction.writeCommandAction(project).compute<PsiClass?, Throwable> {
            try {
                val targetDirectory = directoryResolver.resolveTargetDirectory(project, supplierFile)
                if (targetDirectory == null) {
                    println("Could not determine target directory for TestData class")
                    return@compute null
                }

                val testDataFile = createTestDataFile(project, targetDirectory, testData)
                testDataFile?.classes?.firstOrNull()
            } catch (e: Exception) {
                println("Error creating TestData class: ${e.message}")
                null
            }
        }
    }

    private fun createTestDataFile(
        project: Project,
        targetDirectory: PsiDirectory,
        testData: SupplierOptionsDialog.TestDataClassSelection.CreateNew
    ): PsiJavaFile? {
        val psiFacade = JavaPsiFacade.getInstance(project)

        val className = testData.className
        val packageName = JavaDirectoryService.getInstance().getPackage(targetDirectory)?.qualifiedName ?: ""
        val fileName = "$className.java"

        // Check if file already exists
        if (targetDirectory.findFile(fileName) != null) {
            return targetDirectory.findFile(fileName) as? PsiJavaFile
        }

        val fileContent = """
            package $packageName;
            
            /**
            * TestData class for supplier methods.
            * <br/>
            * Purposes:
            * <ul>
            *   <li>Acts as a data holder for test data (stubs).</li>
            *   <li>Facilitates the creation and management of test data objects for supplier-based tests.</li>
            *   <li>Can be extended with fields and methods to support various test cases and data requirements.</li>
            * </ul>
            */
            public class $className {
               
            }
        """.trimIndent()


        val psiFile = PsiFileFactory.getInstance(project)
            .createFileFromText(fileName, JavaFileType.INSTANCE, fileContent)
        return targetDirectory.add(psiFile) as? PsiJavaFile
    }
}
