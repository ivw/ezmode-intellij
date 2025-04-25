package com.github.ivw.ezmode

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
  }

  companion object {
    private val LOG = logger<EzModeStartupActivity>()
  }
}
