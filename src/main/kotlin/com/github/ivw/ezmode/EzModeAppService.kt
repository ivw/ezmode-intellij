package com.github.ivw.ezmode

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

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

    EditorFactory.getInstance().eventMulticaster.addSelectionListener(
      ModeSelectionListener,
      this,
    )

    ApplicationManager.getApplication().invokeLater {
      initAllEditors(this)
    }
  }

  fun ensureUnloaded() {
    TypedAction.getInstance().ensureEzModeUnloaded()

    clearAllEditorsEzModeData()

    isLoaded = false
  }

  companion object {
    private val LOG = logger<EzModeAppService>()
  }
}
