package com.github.dinbtechit.jetbrainsnestjs

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object NestIcons {
    @JvmField val FileType = IconLoader.getIcon("/icons/nest.svg", javaClass)
    @JvmField val FileTypeController = IconLoader.getIcon("/icons/file-controller.svg", javaClass)
    @JvmField val FileTypeControllerSpec = IconLoader.getIcon("/icons/file-controller-spec.svg", javaClass)
    @JvmField val FileTypeService = IconLoader.getIcon("/icons/file-service.svg", javaClass)
    @JvmField val FileTypeServiceSpec = IconLoader.getIcon("/icons/file-service-spec.svg", javaClass)
    @JvmField val FileTypeModule = IconLoader.getIcon("/icons/file-module.svg", javaClass)
    @JvmField val FileTypeGateway = IconLoader.getIcon("/icons/file-gateway.svg", javaClass)
    @JvmField val FileTypeGatewaySpec = IconLoader.getIcon("/icons/file-gateway-spec.svg", javaClass)
    @JvmField val FileTypeGuard = IconLoader.getIcon("/icons/file-guard.svg", javaClass)
    @JvmField val FileTypeGuardSpec = IconLoader.getIcon("/icons/file-guard-spec.svg", javaClass)
    @JvmField val FileTypePipe = IconLoader.getIcon("/icons/file-pipe.svg", javaClass)
    @JvmField val FileTypePipeSpec = IconLoader.getIcon("/icons/file-pipe-spec.svg", javaClass)
    @JvmField val FileTypeFilter = IconLoader.getIcon("/icons/file-filter.svg", javaClass)
    @JvmField val FileTypeFilterSpec = IconLoader.getIcon("/icons/file-filter-spec.svg", javaClass)
    @JvmField val logo: Icon = IconLoader.getIcon("/icons/nest.svg", javaClass)
    @JvmField val Donate = IconLoader.getIcon("icons/donate.svg", javaClass)
    @JvmField val GitHub = AllIcons.Vcs.Vendors.Github
}
