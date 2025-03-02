package com.github.dinbtechit.jetbrainsnestjs.settings

import com.github.dinbtechit.jetbrainsnestjs.NestBundle
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NestPluginConfigurable : Configurable {
    private val settingShowGenerateOptionFlagsAsCheckboxes = JBCheckBox(NestBundle.message("settings.general.generateOptionFlagsAsCheckboxes"))
    private val settingUseCustomNestJSFileIcons = JBCheckBox(NestBundle.message("settings.general.useCustomNestJSFileIcons"))
    private val settings = SettingsStore.instance

    override fun createComponent(): JComponent {
        return panel {
            group(NestBundle.message("settings.general.group")) {
                row {
                    cell(settingShowGenerateOptionFlagsAsCheckboxes)
                }
                row {
                    cell(settingUseCustomNestJSFileIcons)
                }
            }
        }
    }

    override fun isModified(): Boolean {
        return settings.generateOptionsAsCheckboxes != settingShowGenerateOptionFlagsAsCheckboxes.isSelected ||
               settings.useCustomNestJSFileIcons != settingUseCustomNestJSFileIcons.isSelected
    }

    override fun apply() {
        settings.generateOptionsAsCheckboxes = settingShowGenerateOptionFlagsAsCheckboxes.isSelected
        settings.useCustomNestJSFileIcons = settingUseCustomNestJSFileIcons.isSelected
    }

    override fun reset() {
        settingShowGenerateOptionFlagsAsCheckboxes.isSelected = settings.generateOptionsAsCheckboxes
        settingUseCustomNestJSFileIcons.isSelected = settings.useCustomNestJSFileIcons
    }

    override fun getDisplayName(): String = NestBundle.message("settings.title")
}