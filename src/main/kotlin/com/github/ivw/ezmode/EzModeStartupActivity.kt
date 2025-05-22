package com.github.ivw.ezmode

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.project.*
import com.intellij.openapi.startup.*

/**
 * Executes when opening a project.
 */
class EzModeStartupActivity : ProjectActivity {
  override suspend fun execute(project: Project) {
    LOG.info("Loaded project: ${project.name}")

    service<EzModeAppService>().ensureLoaded()

    ApplicationManager.getApplication().invokeLater {
      project.service<ModeService>().init()
    }
  }

  companion object {
    private val LOG = logger<EzModeStartupActivity>()
  }
}
