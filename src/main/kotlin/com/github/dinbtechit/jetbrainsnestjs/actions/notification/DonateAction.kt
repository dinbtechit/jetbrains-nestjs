package com.github.dinbtechit.jetbrainsnestjs.actions.notification

import com.github.dinbtechit.jetbrainsnestjs.NestIcons
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class DonateAction: DumbAwareAction("Donate ($2)", "", NestIcons.Donate) {

    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.open("https://www.buymeacoffee.com/dinbtechit")
    }

}
