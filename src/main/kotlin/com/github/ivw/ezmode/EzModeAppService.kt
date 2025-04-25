package com.github.ivw.ezmode

import com.github.ivw.ezmode.editor.*
import com.github.ivw.ezmode.keymap.*
import com.intellij.openapi.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

@Service
class EzModeAppService : Disposable {
  private var keyMap: MutableEzModeKeyMap? = null

  override fun dispose() {
    LOG.info("App dispose")
    ensureUnloaded()
  }

  fun ensureLoaded() {
    loadKeymap()

    TypedAction.getInstance().ensureEzModeLoaded()

    EditorFactory.getInstance().eventMulticaster.addSelectionListener(
      ModeSelectionListener,
      this,
    )
  }

  fun ensureUnloaded() {
    keyMap = null

    TypedAction.getInstance().ensureEzModeUnloaded()
  }

  fun loadKeymap(): MutableEzModeKeyMap {
    LOG.info("Loading keymap")

    val keyMap = MutableEzModeKeyMap()
    EzModeRcFileUtils.readResourceRcFile().let { lines ->
      EzModeRcParser.parse(keyMap, lines, null)
    }
    this.keyMap = keyMap
    return keyMap
  }

  fun getKeyMap(): MutableEzModeKeyMap = keyMap ?: loadKeymap()

  companion object {
    private val LOG = logger<EzModeAppService>()
  }
}
