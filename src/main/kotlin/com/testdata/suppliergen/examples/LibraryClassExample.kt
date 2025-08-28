package com.testdata.suppliergen.examples

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.testdata.suppliergen.generator.LibraryClassHandler
import com.testdata.suppliergen.generator.SupplierGenerator
import com.testdata.suppliergen.generator.GenerationContext
import com.testdata.suppliergen.model.InstantiationMode

/**
 * Example demonstrating how the enhanced SupplierGenerator handles library classes.
 * 
 * This example shows how to generate suppliers for common Java library classes like:
 * - java.time.LocalDateTime
 * - java.math.BigDecimal
 * - java.util.UUID
 * - java.net.URI
 */
class LibraryClassExample {

    fun demonstrateLibraryClassGeneration(project: Project) {
        val supplierGenerator = SupplierGenerator(project)
        val libraryHandler = LibraryClassHandler(project)
        val psiFacade = JavaPsiFacade.getInstance(project)
        val scope = GlobalSearchScope.allScope(project)

        // Example 1: Generate supplier for LocalDateTime (from java.time package)
        val localDateTimeClass = psiFacade.findClass("java.time.LocalDateTime", scope)
        if (localDateTimeClass != null) {
            println("Analyzing LocalDateTime class:")
            println("Is library class: ${libraryHandler.isLibraryClass(localDateTimeClass)}")
            
            val fields = libraryHandler.extractFieldsFromLibraryClass(localDateTimeClass)
            println("Extracted ${fields.size} fields from LocalDateTime")
            fields.forEach { field ->
                println("  - ${field.name}: ${field.type} (${field.defaultValue})")
            }
        }

        // Example 2: Generate supplier for BigDecimal
        val bigDecimalClass = psiFacade.findClass("java.math.BigDecimal", scope)
        if (bigDecimalClass != null) {
            println("\nAnalyzing BigDecimal class:")
            println("Is library class: ${libraryHandler.isLibraryClass(bigDecimalClass)}")
            
            val fields = libraryHandler.extractFieldsFromLibraryClass(bigDecimalClass)
            println("Extracted ${fields.size} fields from BigDecimal")
        }

        // Example 3: Demonstrate full generation workflow for library class
        val uuidClass = psiFacade.findClass("java.util.UUID", scope)
        if (uuidClass != null) {
            println("\nGenerating supplier for UUID class:")
            
            // Note: For library classes, you need to provide a target directory
            // since they don't have a source directory
            val context = GenerationContext(
                project = project,
                targetDir = null, // You would set this to your target directory
                maxDepth = 2,
                instantiationMode = InstantiationMode.SETTERS,
                generateInSinglePackage = true,
                testData = null // Not needed for this example
            )
            
            // This would work once you have a proper target directory:
            // val result = supplierGenerator.generateForLibraryClass(context, uuidClass, 0)
            // println("Generated supplier with ${result.dependencies.size} dependencies")
        }
    }
}

/**
 * Usage instructions:
 * 
 * 1. The new LibraryClassHandler can detect library classes automatically
 * 2. It extracts fields using multiple strategies:
 *    - Direct field access (for accessible fields)
 *    - Getter/setter analysis (when fields aren't accessible)
 *    - Fallback to basic Object type (for completely inaccessible classes)
 * 
 * 3. Key benefits:
 *    - Works with decompiled classes (.class files)
 *    - Handles classes from JAR files
 *    - Provides fallback mechanisms when PSI information is incomplete
 *    - Generates meaningful field names from getter/setter methods
 * 
 * 4. Common library classes that now work:
 *    - java.time.* classes (LocalDateTime, LocalDate, etc.)
 *    - java.math.* classes (BigDecimal, BigInteger)
 *    - java.net.* classes (URI, URL)
 *    - java.util.* classes (UUID, Optional, etc.)
 *    - Third-party library classes from dependencies
 */