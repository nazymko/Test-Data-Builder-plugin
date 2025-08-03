//package com.testdata.suppliergen.v2
//
//import com.testdata.suppliergen.SupplierOptionsDialog
//import com.intellij.openapi.actionSystem.AnActionEvent
//import com.intellij.openapi.command.WriteCommandAction
//import com.intellij.openapi.project.Project
//import com.intellij.openapi.roots.ProjectRootManager
//import com.intellij.openapi.vfs.VfsUtilCore
//import com.intellij.openapi.vfs.VirtualFile
//import com.intellij.openapi.vfs.VirtualFileVisitor
//import com.intellij.psi.*
//import org.jetbrains.jps.model.java.JavaSourceRootType
//
//// 15. Main Processing Chain
//class SupplierGenerationProcessor {
//    private val contextExtractor = ActionContextExtractor()
//    private val classResolver = TargetClassResolver()
//    private val optionsHandler = OptionsDialogHandler()
//    private val directoryManager = DirectoryManager()
//    private val contextBuilder = GenerationContextBuilder()
//    private val fileCoordinator = FileGenerationCoordinator()
//    private val destinationResolver = DestinationDirectoryResolver()
//    private val fileProcessor = FileProcessor()
//    private val formatter = CodeStyleFormatter()
//    private val navigator = FileNavigator()
//    private val errorHandler = ErrorHandler()
//
//    fun processAction(e: AnActionEvent) {
//        try {
//            val context = contextExtractor.extractContext(e) ?: return
//            val targetClass = classResolver.resolveTargetClass(context)
//
//            if (targetClass == null) {
//                errorHandler.handleError(context.project, "No Java class found in file.")
//                return
//            }
//
//            val options = optionsHandler.getOptions(context.project) ?: return
//            executeGeneration(context, targetClass, options)
//
//        } catch (ex: Exception) {
//            errorHandler.handleError(e.project, "Exception in GenerateSupplierAction", ex)
//        }
//    }
//
//    private fun executeGeneration(
//        context: ActionContext, targetClass: PsiClass, options: DialogOptions
//    ) {
//        val generateInTest = options.selectedSourceRoot.startsWith("Test")
//        val targetDir = directoryManager.getTargetDirectory(context.psiFile, generateInTest)
//
//        if (targetDir == null) {
//            errorHandler.handleError(
//                context.project,
//                "Could not determine directory for package '${context.psiFile.packageName}'"
//            )
//            return
//        }
//
//        WriteCommandAction.writeCommandAction(context.project)
//            .withName("Generate Supplier").withGroupId("SupplierGenerator")
//            .run<Throwable> {
//                performGeneration(context, targetClass, options, targetDir)
//            }
//    }
//
//    private fun performGeneration(
//        context: ActionContext,
//        targetClass: PsiClass,
//        options: DialogOptions,
//        targetDir: PsiDirectory
//    ) {
//        val ctx = contextBuilder.buildContext(context.project, targetDir, options)
//        val result = fileCoordinator.generateFiles(ctx, context.psiFile, targetClass)
//        val allFiles = listOf(result.root) + result.dependencies
//
//        var lastAddedFile: PsiJavaFile? = null
//
//        allFiles.reversed().distinctBy { it.packageName + it.name }.forEach { file ->
//            val destinationDir = destinationResolver.resolveDestination(ctx, file)
//            val addedFile = fileProcessor.processFile(context.project, destinationDir, file)
//
//            formatter.formatFile(context.project, addedFile, context.psiFile)
//            lastAddedFile = addedFile
//            // Add the initialized() method if test data class was created
//            maybeAddStaticInitializedMethod(
//                project = context.project,
//                testData = ctx.testData,
//                file = addedFile
//            )
//        }
//
//
//        lastAddedFile?.let {
//            navigator.navigateToClass(context.project, it)
//        }
//    }
//
//    private fun maybeAddStaticInitializedMethod(
//        project: Project,
//        testData: SupplierOptionsDialog.TestDataClassSelection?,
//        file: PsiJavaFile?
//    ) {
//        if (testData == null || file == null) return
//
//        val psiFacade = JavaPsiFacade.getInstance(project)
//        val elementFactory = psiFacade.elementFactory
//        val psiManager = PsiManager.getInstance(project)
//        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
//
//        val createdClass = file.classes.firstOrNull() ?: return
//        val className = createdClass.qualifiedName ?: return
//        val supplierSimpleClassName = file.name.removeSuffix(".java")
//        val classNameToUse = supplierSimpleClassName.removeSuffix("Supplier")
//        val methodName = classNameToUse + "ConfiguredBuilder"
//
//        fun hasInitializerMethod(psiClass: PsiClass) =
//            psiClass.methods.any {
//                it.name == methodName &&
//                        it.parameterList.parametersCount == 0 &&
//                        it.hasModifierProperty(PsiModifier.STATIC)
//            }
//
//        fun createInitializeMethod(): PsiMethod {
//            val innerBuilder = supplierSimpleClassName + "Builder"
//            val methodText = """
//            public static $className.$innerBuilder $methodName() {
//                return $className.configuredBuilder();
//            }
//            """
//            println("methodText = ${methodText}")
//            return elementFactory.createMethodFromText(
//                methodText.trimIndent(), createdClass
//            )
//        }
//
//        fun findTestDataPsiClass(fqcn: String): PsiClass? {
//            val testRoots = ProjectRootManager.getInstance(project)
//                .contentSourceRoots
//                .filter { fileIndex.isUnderSourceRootOfType(it, setOf(JavaSourceRootType.TEST_SOURCE)) }
//
//            for (root in testRoots) {
//                var found: PsiClass? = null
//                VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Void>() {
//                    override fun visitFile(file: VirtualFile): Boolean {
//                        if (!file.isDirectory && file.extension == "java") {
//                            psiManager.findFile(file)?.let { psiFile ->
//                                if (psiFile is PsiJavaFile) {
//                                    psiFile.classes.forEach { psiCls ->
//                                        if (psiCls.qualifiedName == fqcn) {
//                                            found = psiCls
//                                            return false // stop
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        return true
//                    }
//                })
//                if (found != null) return found
//            }
//            return null
//        }
//
//        when (testData) {
//            is SupplierOptionsDialog.TestDataClassSelection.UseExisting -> {
//                val psiClass = findTestDataPsiClass(testData.fqClassName)
//                if (psiClass != null) {
//                    if (!hasInitializerMethod(psiClass)) {
//                        WriteCommandAction.runWriteCommandAction(project) {
//                            psiClass.add(createInitializeMethod())
//                        }
//                    }
//                } else {
//                    println("TestData class '${testData.fqClassName}' not found under test sources.")
//                }
//            }
//
//            is SupplierOptionsDialog.TestDataClassSelection.CreateNew -> {
//                val virtual = file.virtualFile
//                if (virtual != null && fileIndex.isInTestSourceContent(virtual)) {
//                    val psiClass = createdClass
//                    if (!hasInitializerMethod(psiClass)) {
//                        WriteCommandAction.runWriteCommandAction(project) {
//                            psiClass.add(createInitializeMethod())
//                        }
//                    }
//                } else {
//                    println("Created TestData file is not located in test sources: ${virtual?.path}")
//                }
//            }
//        }
//    }
//
//
//}