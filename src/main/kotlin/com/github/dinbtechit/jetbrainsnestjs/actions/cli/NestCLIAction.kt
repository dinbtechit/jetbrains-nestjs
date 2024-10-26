package com.github.dinbtechit.jetbrainsnestjs.actions.cli

import com.github.dinbtechit.jetbrainsnestjs.NestIcons
import com.github.dinbtechit.jetbrainsnestjs.actions.cli.store.CLIState
import com.github.dinbtechit.jetbrainsnestjs.common.nestProject.NestProject
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VirtualFile


class NestjsCliAction : DumbAwareAction(NestIcons.logo) {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val nestStoreService = project!!.service<CLIState>()
        val dialog = GenerateCLIDialog(project, e)
        val nestProject = project.service<NestProject>()
        val clickedOk = dialog.showAndGet()
        if (clickedOk) {
            ApplicationManager.getApplication().executeOnPooledThread {
                nestProject.runGenerator(
                    nestStoreService.store.state.project!!,
                    nestStoreService.store.state
                )
            }
        }
    }

    override fun update(e: AnActionEvent) {
        // Display action only if it is a nest project.
        val project = e.project
        val nestProject = project?.service<NestProject>()
        nestProject?.showContextMenu(e)
    }

}
