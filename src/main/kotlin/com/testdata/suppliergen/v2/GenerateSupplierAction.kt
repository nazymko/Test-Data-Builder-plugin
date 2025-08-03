package com.testdata.suppliergen.v2

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

// 1. Main Action Entry Point
class GenerateSupplierAction : AnAction("Generate Supplier"), DumbAware {
    private val processor = com.testdata.suppliergen.v2.v3.SupplierGenerationProcessor()

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        ActionStateValidator().updateActionState(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        processor.processAction(e)
    }
}