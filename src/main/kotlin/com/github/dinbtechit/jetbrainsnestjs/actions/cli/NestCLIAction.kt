package com.github.dinbtechit.jetbrainsnestjs.actions.cli

import com.github.dinbtechit.jetbrainsnestjs.NestIcons
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.Action
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.CLIState
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.GenerateCLIState
import com.intellij.javascript.nodejs.CompletionModuleInfo
import com.intellij.javascript.nodejs.NodeModuleSearchUtil
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.lang.javascript.JavaScriptBundle
import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Files
import java.nio.file.Paths


class NestjsCliAction : DumbAwareAction(NestIcons.logo) {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val nestStoreService = project!!.service<CLIState>()
        val dialog = GenerateCLIDialog(project, e)
        val clickedOk = dialog.showAndGet()
        if (clickedOk) {
            ApplicationManager.getApplication().executeOnPooledThread {
                runGenerator(
                    nestStoreService.store.state.project!!,
                    nestStoreService.store.state
                )
            }
        }
    }

    override fun update(e: AnActionEvent) {
        // Display action only if it is a nest project.
        val project = e.project
        val isProjectOpen = project != null && !project.isDisposed
        var isFileExists = false

        if (isProjectOpen) {
            assert(project != null)
            val projectDirectory: VirtualFile = project!!.guessProjectDir()!!
            val filePath = Paths.get(projectDirectory.path, "nest-cli.json")
            isFileExists = Files.exists(filePath)
        }
        e.presentation.isEnabledAndVisible = isProjectOpen && isFileExists
    }

    private fun runGenerator(
        project: Project,
        schematic: GenerateCLIState
    ) {
        val nestStoreService = project!!.service<CLIState>()
        val interpreter = NodeJsInterpreterManager.getInstance(project).interpreter ?: return

        val modules: MutableList<CompletionModuleInfo> = mutableListOf()
        val cli: VirtualFile = project.guessProjectDir()!!

        NodeModuleSearchUtil.findModulesWithName(modules, "@nestjs/cli", cli, null)

        val module = modules.firstOrNull() ?: return
        val parameters = schematic.parameter.split(" ")
            .toMutableList()
            .map { it.trim() }
            .filter { it != "" }
            .toMutableList()

        nestStoreService.store.dispatch(Action.UpdateOptions(parameters.joinToString(" ")))

        var generateFileWithFullPath = parameters.removeAt(0)
        var runCommandInDir = project.guessProjectDir()

        if (schematic.type != "app" &&
            schematic.type != "sub-app" &&
            schematic.type != "library"
        ) {
            runCommandInDir = schematic.closestModuleDir
            generateFileWithFullPath = "${schematic.filePath}/$generateFileWithFullPath"
        }

        NpmPackageProjectGenerator.generate(
            interpreter, NodePackage(module.virtualFile?.path!!),
            { pkg -> pkg.findBinFile("nest", null)?.absolutePath },
            cli, VfsUtilCore.virtualToIoFile(runCommandInDir ?: cli), project,
            null, JavaScriptBundle.message("generating.0", cli.name),
            arrayOf(), "generate", schematic.type,
            generateFileWithFullPath,
            *parameters.toTypedArray(),
        )

    }
}
