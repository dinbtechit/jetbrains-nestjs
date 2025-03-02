package com.github.dinbtechit.jetbrainsnestjs.actions.cli

import ai.grazie.utils.capitalize
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.Action
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.CLIState
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.util.NestGeneratorFileUtil
import com.github.dinbtechit.jetbrainsnestjs.settings.SettingsStore
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.ComboboxSpeedSearch
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.ComboBoxPredicate
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.util.ui.UIUtil
import java.awt.event.ItemEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

class GenerateCLIDialog(private val project: Project, val e: AnActionEvent, val type: String? = null) :
    DialogWrapper(project) {
    private val autoCompleteField = TextFieldWithAutoCompletion(
        project,
        CLIOptionsCompletionProvider(CLIOptionsCompletionProvider.options.keys.toList()), false,
        null
    ).apply {
        setPlaceholder("filename --options")
    }

    private val optionCheckBoxes = CLIOptionsCompletionProvider.options
        .filter { it.key.startsWith("--") }
        .map { (option, description) ->
            val name = option.removePrefix("--").split("-")
                .joinToString(" ") { it.capitalize() }
            JBCheckBox(name, false).apply {
                toolTipText = description
            }
        }

    private val nestStoreService = project.service<CLIState>()
    private val generatePath = JBTextField()
    private val moduleLocation = JBTextField()

    private val comboBoxModel = DefaultComboBoxModel(
        CLIOptionsCompletionProvider.generateItems.keys.toTypedArray()
    )
    private val comboBox = ComboBox(comboBoxModel).apply {
        setRenderer(GenerateTypeComboRenderer())
    }
    private val warningLabel = JBLabel(
        "Converts to monorepo if it's a standard structure",
        AllIcons.General.Warning, JBLabel.LEFT
    )
    private val moduleInfoLabel = JBLabel(
        "ModulePath",
        AllIcons.General.Information, JBLabel.LEFT
    )
    private val noModuleFoundWarningLabel = JBLabel(
        "No module found to update",
        AllIcons.General.Warning, JBLabel.LEFT
    )
    private val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

    private val directory = if (virtualFile != null )when {
        virtualFile.isDirectory -> virtualFile // If it's directory, use it
        else -> virtualFile.parent // Otherwise, get its parent directory
    } else project.guessProjectDir()

    init {
        title = if (type != null) "Nest ${type.capitalize()} Generate" else "Nest CLI/Schematics Generate"
        val state = nestStoreService.store.getState()
        comboBox.item = type ?: (state.type ?: "controller")
        autoCompleteField.text = if (type == null) state.parameter else ""
        generatePath.text = NestGeneratorFileUtil.computeGeneratePath(comboBox.item, project, directory!!)
        generatePath.isEnabled = false
        moduleInfoLabel.text = """<html><b>Updates Module:</b> 
            |${
            NestGeneratorFileUtil.getRelativePath(
                project,
                NestGeneratorFileUtil.findClosestModuleFile(project, e, directory!!)
            )
        } </html>
            |""".trimMargin()
        moduleLocation.isEnabled = false

        warningLabel.isVisible = isAppOrLibrarySelected()
        init()

        setupListeners()
    }

    private fun setupListeners() {
        setupComboBoxListener()
        setupOptionCheckBoxListeners()
        setupAutoCompleteFieldListener()
    }

    private fun setupComboBoxListener() {
        comboBox.addItemListener {
            if (it?.stateChange == ItemEvent.SELECTED) {
                warningLabel.isVisible = isAppOrLibrarySelected()
                directory?.let { dir ->
                    generatePath.text = NestGeneratorFileUtil.computeGeneratePath(comboBox.item, project, dir)
                }
            }
        }
        ComboboxSpeedSearch.installSpeedSearch(comboBox) { comboBox.item }
    }

    private fun setupOptionCheckBoxListeners() {
        optionCheckBoxes.forEach { checkBox ->
            val option = "--${checkBox.text.lowercase().replace(" ", "-")}"
            checkBox.addItemListener { event ->
                handleCheckBoxStateChange(event, option)
            }
        }
    }

    private fun handleCheckBoxStateChange(event: ItemEvent, option: String) {
                val currentText = autoCompleteField.text
                when (event.stateChange) {
                    ItemEvent.SELECTED -> {
                        if (!currentText.contains(option)) {
                            val baseText = currentText.trim()
                            autoCompleteField.text = if (baseText.isNotEmpty()) {
                                "$baseText $option"
                            } else {
                                option
                            }
                        }
                    }
                    ItemEvent.DESELECTED -> {
                        autoCompleteField.text = currentText
                            .replace(option, "")
                            .replace("  ", " ")
                            .trim()
                    }
                }
            }

    private fun setupAutoCompleteFieldListener() {
        autoCompleteField.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                val currentText = autoCompleteField.text
                optionCheckBoxes.forEach { checkBox ->
                    val option = "--${checkBox.text.lowercase().replace(" ", "-")}"
                    checkBox.isSelected = currentText.contains(option)
                }
            }
        })
    }

    private fun isAppOrLibrarySelected(): Boolean {
        return comboBox.selectedItem == "app"
                || comboBox.selectedItem == "library"
                || comboBox.selectedItem == "sub-app"
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("Generate Path:") {}.visible(generatePath.text.trim().isNotBlank())
            row {
                cell(generatePath).align(Align.FILL)
            }.visible(generatePath.text.trim().isNotBlank())

            row("Type:") {}.topGap(TopGap.SMALL).visible(type == null)
            row {
                cell(comboBox).align(Align.FILL)
            }.visible(type == null)
            row {
                cell(warningLabel.apply {
                    font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)
                }).align(Align.FILL)
            }

            row("Parameters:") {}.topGap(TopGap.SMALL)
            row {
                cell(autoCompleteField).align(Align.FILL)
            }
            row {
                val spaces = " "
                cell(JBLabel("$spaces Filename --options").apply {
                    font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)
                })
            }

            group("Generation Options") {
                for (checkBox in optionCheckBoxes) {
                    row {
                        cell(checkBox)
                    }
                }
            }.topGap(TopGap.SMALL)
             .visibleIf(object : ComponentPredicate() {
                 override fun invoke(): Boolean = SettingsStore.instance.generateOptionsAsCheckboxes
                 override fun addListener(listener: (Boolean) -> Unit) {}
             })

            val showModuleLocation = ComboBoxPredicate(comboBox) {
                it != "app" && it != "library"
                        && it != "sub-app"
                        && it != "configuration"
                        && it != "filter"
                        && it != "guard"
                        && it != "interceptor"
                        && it != "interface"
                        && it != "middleware"
                        && it != "class"
                        && it != "pipe"
                        && it != "decorator"
            }

            row {
                cell(moduleInfoLabel.apply {
                    font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)
                }).align(Align.FILL).visible(moduleInfoLabel.text.contains("module.ts"))
                cell(noModuleFoundWarningLabel.apply {
                    font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)
                }).align(Align.FILL).visible(
                    !moduleInfoLabel.text.contains("module.ts")
                            && generatePath.text.trim().isNotBlank()
                )
            }.visibleIf(showModuleLocation)
                .visibleIf(TextComponentPredicate(autoCompleteField) {
                    !it.contains("--skip-import")
                })
        }
    }

    override fun doValidate(): ValidationInfo? {
        val fileName = autoCompleteField.text.split(" ")[0]
        var invalidFileName = false
        if (fileName.isNotBlank() && fileName.startsWith("-", ignoreCase = true)) {
            invalidFileName = true
        }
        return if (fileName.isBlank() || autoCompleteField.text.isBlank()) {
            ValidationInfo("Filename cannot be blank", autoCompleteField)
        } else if (invalidFileName) {
            ValidationInfo("$fileName in an invalid filename", autoCompleteField)
        } else null
    }

    override fun doOKAction() {
        nestStoreService.store.dispatch(
            Action.GenerateCLIAction(
                type = type ?: comboBox.item,
                options = autoCompleteField.text,
                filePath = NestGeneratorFileUtil.getFilePath(project, e, directory!!),
                project = project,
                generateInDir = directory,
                closestModuleDir = NestGeneratorFileUtil.findClosestModuleFileDir(project, e, directory)
            )
        )
        super.doOKAction()
    }

    private class TextComponentPredicate(
        private val component: TextFieldWithAutoCompletion<String>,
        private val predicate: (String) -> Boolean
    ) : ComponentPredicate() {
        override fun invoke(): Boolean = predicate(component.text)

        override fun addListener(listener: (Boolean) -> Unit) {
            component.document.addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    listener(invoke())
                }
            })
        }
    }
}