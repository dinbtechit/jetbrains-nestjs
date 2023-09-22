package com.github.dinbtechit.jetbrainsnestjs.actions.cli.store

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class GenerateCLIState(
    val type: String? = null,
    val parameter: String = "",
    val project: Project? = null,
    val filePath: String = "",
    val generateInDir: VirtualFile? = null,
    val closestModuleDir: VirtualFile? = null

)

