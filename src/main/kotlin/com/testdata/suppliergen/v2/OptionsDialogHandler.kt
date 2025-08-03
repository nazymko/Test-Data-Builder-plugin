package com.testdata.suppliergen.v2
// 5. User Options Dialog Handler

import com.testdata.suppliergen.SupplierOptionsDialog
import com.intellij.openapi.project.Project


class OptionsDialogHandler {
    fun getOptions(project: Project): DialogOptions? {
        val dialog = SupplierOptionsDialog(project)
        if (!dialog.showAndGet()) return null

        return DialogOptions(
            dialog.getSelectedSourceRoot(),
            dialog.shouldGenerateInSamePackage(),
            dialog.getSelectedInstantiationMode(),
            dialog.getSelectedTestDataClassInfo(),
            dialog.getMaxDepth()
        )
    }
}