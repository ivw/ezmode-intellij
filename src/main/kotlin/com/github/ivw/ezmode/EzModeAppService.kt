package com.github.ivw.ezmode

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

@Service
class EzModeAppService : Disposable {
  override fun dispose() {
    LOG.info("App dispose")
    ensureUnloaded()
  }

  fun ensureLoaded() {
    TypedAction.getInstance().ensureEzModeLoaded()

    EditorFactory.getInstance().eventMulticaster.addSelectionListener(
      ModeSelectionListener,
      this,
    )
  }

  fun ensureUnloaded() {
    TypedAction.getInstance().ensureEzModeUnloaded()
  }

  companion object {
    private val LOG = logger<EzModeAppService>()
  }
}
