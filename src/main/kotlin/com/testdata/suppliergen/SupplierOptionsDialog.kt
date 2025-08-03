package com.testdata.suppliergen

import ai.grazie.utils.attributes.value
import com.testdata.suppliergen.model.InstantiationMode
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.FormBuilder
import org.jetbrains.jps.model.java.JavaSourceRootType
import javax.swing.*

class SupplierOptionsDialog(project: Project) : DialogWrapper(project) {

    private val srcOptions = arrayOf(
        "Test (src/test/java)", "Main (src/main/java)"
    )
    private val srcRootsCombo = JComboBox(srcOptions)
    private val samePackageCheckBox = JCheckBox("Generate ALL suppliers in same package as target class", false)

    private val builderRadio = JRadioButton("Builder (@Builder from lombok)", true)
    private val setterRadio = JRadioButton("Setter (No arg constructor required)")
    private val constructorRadio = JRadioButton("Constructor (All arg constructor required)")
    private val modeGroup = ButtonGroup()

    private val testDataCombo = JComboBox<String>()
    private val newTestDataField = JTextField("TestData", 10).apply {
        toolTipText = "Name of the new TestData class to create"
        isVisible = false
        isEditable = false
        isEnabled = false
    }
    private val NEW_TESTDATA_OPTION = "<Create New TestData>"
    private val DO_NOT_CREATE_TEST_DATA = "<Do Not Use/Create TestData>"
    private val maxDepthSpinner = JSpinner(SpinnerNumberModel(5, 1, 20, 1))

    init {
        title = "Generate Supplier Options"
        init()

        // Group radio buttons
        modeGroup.add(builderRadio)
        modeGroup.add(setterRadio)
        modeGroup.add(constructorRadio)

        // Style srcRoots combo box
        srcRootsCombo.renderer = object : ColoredListCellRenderer<String>() {
            override fun customizeCellRenderer(
                list: JList<out String>, value: String?, index: Int, selected: Boolean, hasFocus: Boolean
            ) {
                if (value == null) return
                if (value.contains("Test")) {
                    append(value, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
                } else {
                    append(value, SimpleTextAttributes.GRAYED_ATTRIBUTES)
                }
            }
        }

        // Populate TestData options dynamically
        val classes = findTestDataClasses(project)
        testDataCombo.model =
            DefaultComboBoxModel((classes + NEW_TESTDATA_OPTION + DO_NOT_CREATE_TEST_DATA).toTypedArray())

        testDataCombo.selectedItem = if (classes.getOrNull(0) == null) {
            NEW_TESTDATA_OPTION
        } else {
            classes[0]
        }

        newTestDataField.isVisible = true

        testDataCombo.addActionListener {
            newTestDataField.isVisible = (testDataCombo.selectedItem == NEW_TESTDATA_OPTION)
        }
    }

    override fun createCenterPanel(): JComponent {
        return FormBuilder.createFormBuilder().addLabeledComponent(JLabel("Select target source root:"), srcRootsCombo)
            .addSeparator().addComponent(JLabel("Select instantiation mode:")).addComponent(builderRadio)
            .addComponent(setterRadio).addComponent(constructorRadio).addSeparator().addComponent(samePackageCheckBox)
            .addSeparator().addLabeledComponent(JLabel("Select or create TestData class:"), testDataCombo)
            .addLabeledComponent(JLabel("New TestData class name:"), newTestDataField).addSeparator()
            .addLabeledComponent(JLabel("Max nested depth for supplier generation:"), maxDepthSpinner)
            .panel
    }

    fun getMaxDepth(): Int = maxDepthSpinner.value as Int

    fun shouldGenerateInSamePackage(): Boolean = samePackageCheckBox.isSelected

    fun getSelectedSourceRoot(): String = srcRootsCombo.selectedItem as String

    fun getSelectedInstantiationMode(): InstantiationMode = when {
        builderRadio.isSelected -> InstantiationMode.BUILDER
        setterRadio.isSelected -> InstantiationMode.SETTERS
        constructorRadio.isSelected -> InstantiationMode.CONSTRUCTOR
        else -> InstantiationMode.BUILDER
    }

    fun getSelectedTestDataClassInfo(): TestDataClassSelection =
        if (testDataCombo.selectedItem == NEW_TESTDATA_OPTION) {
            TestDataClassSelection.CreateNew(newTestDataField.text.trim())
        } else {
            TestDataClassSelection.UseExisting(testDataCombo.selectedItem as String)
        }

    sealed interface TestDataClassSelection {
        data class UseExisting(val fqClassName: String) : TestDataClassSelection
        data class CreateNew(val className: String) : TestDataClassSelection
    }

    fun findTestDataClasses(project: Project): List<String> {
        val projectRootManager = ProjectRootManager.getInstance(project)
        val fileIndex = projectRootManager.fileIndex
        val psiManager = PsiManager.getInstance(project)

        val testSourceRoots = projectRootManager.contentSourceRoots.filter {
            fileIndex.isUnderSourceRootOfType(it, setOf(JavaSourceRootType.TEST_SOURCE))
        }

        val result = mutableListOf<String>()

        for (root in testSourceRoots) {
            VfsUtilCore.visitChildrenRecursively(root, object : VirtualFileVisitor<Void>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (!file.isDirectory && file.extension == "java") {
                        val psiFile = psiManager.findFile(file)
                        if (psiFile is PsiJavaFile) {
                            psiFile.classes.filter { it.name?.endsWith("TestData") == true }.forEach { psiClass ->
                                psiClass.qualifiedName?.let { result.add(it) }
                            }
                        }
                    }
                    return true
                }
            })
        }

        return result.sorted()
    }
}
