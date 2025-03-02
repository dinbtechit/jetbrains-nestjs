package com.github.dinbtechit.jetbrainsnestjs.settings

import com.github.dinbtechit.jetbrainsnestjs.NestBundle
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NestPluginConfigurable : Configurable {
    private val settingShowGenerateOptionFlagsAsCheckboxes = JBCheckBox(NestBundle.message("settings.general.generateOptionFlagsAsCheckboxes"))
    private val settings = SettingsStore.instance

    override fun createComponent(): JComponent {
        return panel {
            group(NestBundle.message("settings.general.group")) {
                row {
                    cell(settingShowGenerateOptionFlagsAsCheckboxes)
                }
            }
        }
    }

    override fun isModified(): Boolean {
        return settings.generateOptionsAsCheckboxes != settingShowGenerateOptionFlagsAsCheckboxes.isSelected
    }

    override fun apply() {
        settings.generateOptionsAsCheckboxes = settingShowGenerateOptionFlagsAsCheckboxes.isSelected
    }

    override fun reset() {
        settingShowGenerateOptionFlagsAsCheckboxes.isSelected = settings.generateOptionsAsCheckboxes
    }

    override fun getDisplayName(): String = NestBundle.message("settings.title")
}