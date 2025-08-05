package com.testdata.suppliergen.generator.sections

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.testdata.suppliergen.generator.SectionBuilder
import com.testdata.suppliergen.model.SupplierClassModel

class ClassHeaderSection : SectionBuilder {
    override fun render(model: SupplierClassModel): String {
        val timestamp = java.time.Instant.now().toString()
        val info = getPluginInfo()

        return """
        /**
         * Utility class to assemble {@link ${model.targetClassName}} object.
         */
        @Data
        @Builder
        @Generated(
            value = "${info.name} v${info.version}",
            date = "$timestamp", 
            comments = "Auto-generated supplier for ${model.targetClassName} class by Andrew's '${info.name}' plugin. Source: ${model.targetQualifiedName}"
        )
        public class ${model.supplierClassName} implements Supplier<${model.targetClassName}> {
    """.trimIndent() + "\n"
    }

    fun getPluginInfo(): PluginInfo {
        return try {
            val pluginId = "com.testdata.supplier.generator"
            val plugin = PluginManagerCore.getPlugin(PluginId.getId(pluginId))
            PluginInfo(
                name = plugin?.name ?: "unknown",
                version = plugin?.version ?: "unknown"
            )
        } catch (e: Exception) {
            PluginInfo(name = "unknown", version = "unknown")
        }
    }

    data class PluginInfo(
        val name: String,
        val version: String
    )
}