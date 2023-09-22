package com.github.dinbtechit.jetbrainsnestjs

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object NestIcons {
    @JvmField
    val FileType = IconLoader.getIcon("/icons/type.svg", javaClass)
    @JvmField
    val logo: Icon = IconLoader.getIcon("/icons/nest.svg", javaClass)
    @JvmField
    val Donate = IconLoader.getIcon("icons/donate.svg", javaClass)
    @JvmField
    val GitHub = AllIcons.Vcs.Vendors.Github
}
