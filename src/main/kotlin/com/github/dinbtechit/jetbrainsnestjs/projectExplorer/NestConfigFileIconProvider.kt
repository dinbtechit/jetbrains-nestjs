package com.github.dinbtechit.jetbrainsnestjs.projectExplorer

import com.github.dinbtechit.jetbrainsnestjs.NestIcons
import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

class NestConfigFileIconProvider : IconProvider(), DumbAware {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val fileElement = element as? PsiFile
        return if (fileElement != null) {
            when {
                fileElement.name.equals("nest-cli.json", true) -> NestIcons.FileType
                else -> null
            }
        } else null
    }
}
