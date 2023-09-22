package com.github.dinbtechit.jetbrainsnestjs.actions.notification


import com.github.dinbtechit.jetbrainsnestjs.NestIcons
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class StarGithubRepoAction: DumbAwareAction("Star Repo", "", NestIcons.GitHub) {

    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.open("https://github.com/dinbtechit/ngxs")
    }

}
