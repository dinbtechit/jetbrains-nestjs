package com.github.dinbtechit.jetbrainsnestjs.projectExplorer

import com.github.dinbtechit.jetbrainsnestjs.NestIcons
import com.github.dinbtechit.jetbrainsnestjs.common.nestProject.NestProject
import com.intellij.ide.IconProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

class NestConfigFileIconProvider : IconProvider(), DumbAware {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val fileElement = element as? PsiFile
        val nestProject = element.project.service<NestProject>()
        return if (fileElement != null && nestProject.isNestProject(element.project)) {
            when {
                fileElement.name.equals("nest-cli.json", true) -> NestIcons.FileType
                fileElement.name.contains(".controller.ts", true) -> NestIcons.FileTypeController
                fileElement.name.contains(".controller.spec.ts", true) -> NestIcons.FileTypeControllerSpec
                fileElement.name.contains(".service.ts", true) -> NestIcons.FileTypeService
                fileElement.name.contains(".service.spec.ts", true) -> NestIcons.FileTypeServiceSpec
                fileElement.name.contains(".module.ts", true) -> NestIcons.FileTypeModule
                fileElement.name.contains(".filter.ts", true) -> NestIcons.FileTypeFilter
                fileElement.name.contains(".filter.spec.ts", true) -> NestIcons.FileTypeFilterSpec
                fileElement.name.contains(".guard.ts", true) -> NestIcons.FileTypeGuard
                fileElement.name.contains(".guard.spec.ts", true) -> NestIcons.FileTypeGuardSpec
                fileElement.name.contains(".interceptor.ts", true) -> NestIcons.FileTypeGuard
                fileElement.name.contains(".interceptor.spec.ts", true) -> NestIcons.FileTypeGuardSpec
                fileElement.name.contains(".middleware.ts", true) -> NestIcons.FileTypeGuard
                fileElement.name.contains(".middleware.spec.ts", true) -> NestIcons.FileTypeGuardSpec
                fileElement.name.contains(".pipe.ts", true) -> NestIcons.FileTypePipe
                fileElement.name.contains(".pipe.spec.ts", true) -> NestIcons.FileTypePipeSpec
                fileElement.name.contains(".gateway.ts", true) -> NestIcons.FileTypeGateway
                fileElement.name.contains(".gateway.spec.ts", true) -> NestIcons.FileTypeGatewaySpec
                fileElement.name.contains(".resolver.ts", true) -> NestIcons.FileTypeGateway
                fileElement.name.contains(".resolver.spec.ts", true) -> NestIcons.FileTypeGatewaySpec
                fileElement.name.contains(".decorator.ts", true) -> NestIcons.FileTypeGateway
                fileElement.name.contains(".dto.ts", true) -> NestIcons.FileTypeFilter
                else -> null
            }
        } else null
    }
}
