package com.github.dinbtechit.jetbrainsnestjs.actions.cli.store

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import org.reduxkotlin.Reducer
import org.reduxkotlin.threadsafe.createThreadSafeStore

@Service(Service.Level.PROJECT)
class CLIState(project: Project) {
    val store = createThreadSafeStore(reducer(), GenerateCLIState())
    private fun reducer(): Reducer<GenerateCLIState> {
        return { state, action ->
            when (action) {
                is Action.GenerateCLIAction -> {
                    GenerateCLIState(
                        type = action.type,
                        parameter = action.options,
                        filePath = action.filePath,
                        project = action.project,
                        closestModuleDir = action.closestModuleDir,
                        generateInDir = action.generateInDir
                    )
                }

                is Action.UpdateOptions -> {
                    state.copy(
                        parameter = action.options
                    )
                }

                else -> state
            }
        }
    }
}
