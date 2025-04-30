package com.github.ivw.ezmode.keymap

import com.intellij.openapi.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.util.messages.*

@Service
class EzModeKeyMapAppService {
  private var keyMap: MutableEzModeKeyMap? = null

  val application: Application = ApplicationManager.getApplication()

  fun loadKeymap(): MutableEzModeKeyMap =
    EzModeRcFileUtils.getKeyMap().also {
      keyMap = it
      application.messageBus.syncPublisher(KEYMAP_CHANGE_TOPIC)
        .onChanged(it)
    }

  fun getKeyMap(): MutableEzModeKeyMap = keyMap ?: loadKeymap()

  fun subscribeToKeyMap(parentDisposable: Disposable, onChanged: (MutableEzModeKeyMap) -> Unit) {
    application.messageBus.connect(parentDisposable).subscribe(
      KEYMAP_CHANGE_TOPIC,
      object : KeyMapChangeNotifier {
        override fun onChanged(keyMap: MutableEzModeKeyMap) {
          onChanged(keyMap)
        }
      }
    )
  }

  interface KeyMapChangeNotifier {
    fun onChanged(keyMap: MutableEzModeKeyMap)
  }

  companion object {
    @Topic.AppLevel
    val KEYMAP_CHANGE_TOPIC = Topic(KeyMapChangeNotifier::class.java)
  }
}
