package com.github.dinbtechit.jetbrainsnestjs.common.nestProject

import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.Action
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.CLIState
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.GenerateCLIState
import com.intellij.javascript.nodejs.CompletionModuleInfo
import com.intellij.javascript.nodejs.NodeModuleSearchUtil
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.lang.javascript.JavaScriptBundle
import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Files
import java.nio.file.Paths

@Service(Service.Level.PROJECT)
class NestProject {

    fun isNestProject(project: Project?): Boolean {
        val isProjectOpen = project != null && !project.isDisposed
        var isFileExists = false

        if (isProjectOpen) {
            assert(project != null)
            val projectDirectory: VirtualFile = project!!.guessProjectDir()!!
            val filePath = Paths.get(projectDirectory.path, "nest-cli.json")
            isFileExists = Files.exists(filePath)
            return isFileExists
        }
        return false
    }

    fun runGenerator(
        project: Project,
        schematic: GenerateCLIState
    ) {
        val nestStoreService = project.service<CLIState>()
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