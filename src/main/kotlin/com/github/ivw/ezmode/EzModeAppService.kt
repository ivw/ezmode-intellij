package com.github.ivw.ezmode

import com.github.ivw.ezmode.config.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.actionSystem.*

/**
 * Handles the loading/unloading of the plugin.
 */
@Service
class EzModeAppService : Disposable {
  private var isLoaded = false

  override fun dispose() {
    LOG.info("App dispose")
    ensureUnloaded()
  }

  fun ensureLoaded() {
    if (isLoaded) return
    isLoaded = true

    TypedAction.getInstance().ensureEzModeLoaded()

    moveTabShortcuts()
  }

  fun ensureUnloaded() {
    if (!isLoaded) return
    isLoaded = false

    TypedAction.getInstance().ensureEzModeUnloaded()

    clearAllEditorsEzModeData()

    restoreTabShortcuts()
  }

  companion object {
    private val LOG = logger<EzModeAppService>()
  }
}
