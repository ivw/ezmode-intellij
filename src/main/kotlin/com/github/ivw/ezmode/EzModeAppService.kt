package com.github.ivw.ezmode

import com.github.ivw.ezmode.editor.*
import com.github.ivw.ezmode.keymap.*
import com.intellij.openapi.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.util.messages.*

@Service
class EzModeAppService : Disposable {
  private var keyMap: MutableEzModeKeyMap? = null

  val application: Application = ApplicationManager.getApplication()

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

  fun loadKeymap(): MutableEzModeKeyMap =
    EzModeRcFileUtils.getKeyMap().also {
      keyMap = it
      application.messageBus.syncPublisher(KEYMAP_CHANGE_TOPIC)
        .onChanged(it)
    }

  fun getKeyMap(): MutableEzModeKeyMap = keyMap ?: loadKeymap()

  fun subscribeToKeyMap(parentDisposable: Disposable, onChange: (MutableEzModeKeyMap) -> Unit) {
    application.messageBus.connect(parentDisposable).subscribe(
      KEYMAP_CHANGE_TOPIC,
      object : KeyMapChangeNotifier {
        override fun onChanged(keyMap: MutableEzModeKeyMap) {
          onChange(keyMap)
        }
      }
    )
  }

  interface KeyMapChangeNotifier {
    fun onChanged(keyMap: MutableEzModeKeyMap)
  }

  companion object {
    private val LOG = logger<EzModeAppService>()

    @Topic.AppLevel
    val KEYMAP_CHANGE_TOPIC = Topic(KeyMapChangeNotifier::class.java)
  }
}
