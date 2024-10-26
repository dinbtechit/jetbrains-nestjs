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
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

@Service(Service.Level.PROJECT)
class NestProject {

    fun isNestProject(project: Project?): Boolean {
        val isProjectOpen = project != null && !project.isDisposed
        if (isProjectOpen) {
            return getAllNestJSCliFiles(project!!).isNotEmpty()
        }
        return false
    }

    fun showContextMenu(e: AnActionEvent) {
        val project = e.project
        val nestProject = project?.service<NestProject>()
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (project == null || virtualFile == null) {
            e.presentation.isEnabledAndVisible = false // Disable the action if required data is unavailable
            return
        }
        e.presentation.isEnabledAndVisible = nestProject?.isWithinNestProjectFolder(e) == true
    }

    fun isWithinNestProjectFolder(e: AnActionEvent): Boolean {
        val virtualFile: VirtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE)

        if (e.project == null) return false

            val directory = when {
                virtualFile.isDirectory -> virtualFile // If it's directory, use it
                else -> virtualFile.parent // Otherwise, get its parent directory
            }
            val nestCliFiles = getAllNestJSCliFiles(e.project!!)
            for (showContext in nestCliFiles) {
                if (directory.path.contains(showContext.parent.path)) {
                    return true
                }
            }
            return false
    }

    fun isFileWithinNestProject(file: PsiFile): Boolean {
        val nestCliFiles = getAllNestJSCliFiles(file.project)
        for (showContext in nestCliFiles) {
            if (file.virtualFile.path.contains(showContext.parent.path)) {
                return true
            }
        }
        return false
    }

    private fun getAllNestJSCliFiles(project: Project): MutableCollection<VirtualFile> {
        return FilenameIndex.getVirtualFilesByName("nest-cli.json", GlobalSearchScope.projectScope(project))
    }

    fun runGenerator(
        project: Project,
        schematic: GenerateCLIState
    ) {
        val nestStoreService = project.service<CLIState>()
        val interpreter = NodeJsInterpreterManager.getInstance(project).interpreter ?: return

        val modules: MutableList<CompletionModuleInfo> = mutableListOf()
        val cli: VirtualFile = project.guessProjectDir()!!

        val closestPackageJsonFile = PackageJsonUtil.findUpPackageJson(schematic.generateInDir!!)

        NodeModuleSearchUtil.findModulesWithName(modules, "@nestjs/cli", closestPackageJsonFile, null)

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