# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IntelliJ IDEA plugin written in Kotlin that generates supplier-based test data builders for Java classes using the Test Data Builder pattern. The plugin allows developers to right-click on Java classes and generate corresponding Supplier classes that provide pre-configured test data.

## Build System & Development Commands

### Core Gradle Commands
- `./gradlew build` - Build the entire plugin
- `./gradlew test` - Run all tests  
- `./gradlew clean` - Clean build artifacts
- `./gradlew buildPlugin` - Build the plugin ZIP for distribution
- `./gradlew runIde` - Launch IntelliJ IDEA with the plugin installed for testing
- `./gradlew verifyPlugin` - Validate plugin structure and compatibility

### Development Workflow
- `./gradlew prepareSandbox` - Prepare sandbox environment for testing
- `./gradlew instrumentCode` - Execute code instrumentation for IntelliJ platform
- `./gradlew patchPluginXml` - Update plugin.xml with build information

## Architecture

### Package Structure
- **Main entry point**: `GenerateSupplierAction` in `com.testdata.suppliergen.v2` - handles the Ctrl+Shift+S action
- **Core generation**: `SupplierGenerator` in `com.testdata.suppliergen.generator` - main generation logic
- **Processing pipeline**: `com.testdata.suppliergen.v2.v3` contains the latest generation processors
- **Type handling**: `com.testdata.suppliergen.types` - handles different Java types (primitives, collections, maps, etc.)
- **Code sections**: `com.testdata.suppliergen.generator.sections` - modular code generation components

### Key Components

#### Generation Pipeline (v3)
- `SupplierGenerationProcessor` - Main coordinator for generation process
- `TestDataClassFinder` - Locates and validates target classes 
- `GenerationExecutor` - Executes the generation workflow
- `TestDataMethodAdder` - Adds methods to existing test data classes

#### Type System
- `TypeHandlerRegistry` - Central registry for all type handlers
- Handlers for primitives, collections, maps, enums, dates, optionals
- Special handling for library classes vs source classes
- Recursive generation for nested custom types

#### Code Generation Sections
Modular system where each section generates a specific part of the supplier class:
- `PackageSection`, `ImportSection`, `ClassHeaderSection`
- `FieldSection`, `ConfiguredBuilderSection`, `GetMethodSection`
- `InitializedSection`, `AdditionalMethodsSection`, `AssertEqualSection`

### Plugin Configuration
- Plugin ID: `com.testdata.supplier.generator`
- Action ID: `GenerateSupplierAction` 
- Keyboard shortcuts: Ctrl+Shift+S (Windows/Linux), Cmd+Shift+S (macOS)
- Requires IntelliJ Platform 2023.1+ and Java 11+

### Key Features
- Recursive supplier generation for nested custom types
- Support for both source classes and library classes (JAR dependencies)
- Configurable instantiation modes (Builder, Constructor, Setter)
- Option to generate all suppliers in single package or preserve structure
- Threading-safe dialog handling during generation
- Comprehensive type support including Guava collections, Java time APIs, etc.

## Development Notes

### Plugin Dependencies
- Targets IntelliJ Community Edition 2023.1
- Requires `com.intellij.java` and `org.jetbrains.kotlin` bundled plugins
- Uses IntelliJ Platform Gradle Plugin 2.7.0

### Code Generation Strategy
The plugin uses a section-based approach where each `SectionBuilder` is responsible for generating a specific part of the supplier class. This makes the code generation modular and maintainable.

### Library Class Handling
Special logic in `LibraryClassHandler` allows generation of suppliers for classes from JAR files and external libraries, not just source code in the current project.