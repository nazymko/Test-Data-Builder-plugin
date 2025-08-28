package com.testdata.suppliergen.v2

import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.generator.DirectoryValidator
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager

// 10. Destination Directory Resolver
class DestinationDirectoryResolver {
    private val packageHandler = PackageDirectoryHandler()
    private val logger = Logger.getInstance(DestinationDirectoryResolver::class.java)

    fun resolveDestination(ctx: GenerationContext, file: PsiJavaFile): PsiDirectory {
        val targetDir = ctx.targetDir ?: throw IllegalStateException("Target directory is null")
        
        // Always use target directory for single package mode
        if (ctx.generateInSinglePackage) {
            logger.debug("Using target directory for single package mode: ${targetDir.virtualFile.path}")
            return targetDir
        }

        // Try to resolve package-specific directory
        return try {
            val testSourceRoot = ProjectRootManager.getInstance(ctx.project).fileIndex
                .getSourceRootForFile(targetDir.virtualFile)
            
            if (testSourceRoot == null) {
                logger.debug("Unable to determine source root, using target directory")
                return targetDir
            }

            val psiManager = PsiManager.getInstance(ctx.project)
            val testRootDir = psiManager.findDirectory(testSourceRoot)
            
            if (testRootDir == null) {
                logger.debug("Unable to find PSI directory for source root, using target directory")
                return targetDir
            }

            val packageDir = packageHandler.createOrFindDirectoryForPackage(
                ctx.project, testRootDir, file.packageName
            )
            
            // Only check for actual library directories, not project build directories
            if (DirectoryValidator.isLibraryDirectory(packageDir)) {
                logger.info("Package directory is library location, using target directory: ${packageDir.virtualFile.path}")
                targetDir
            } else if (!DirectoryValidator.isWritableDirectory(packageDir)) {
                logger.info("Package directory is not writable, using target directory: ${packageDir.virtualFile.path}")
                targetDir
            } else {
                logger.debug("Using package directory: ${packageDir.virtualFile.path}")
                packageDir
            }
        } catch (e: Exception) {
            logger.warn("Error resolving package directory for ${file.packageName}, using target directory: ${e.message}")
            targetDir
        }
    }
}