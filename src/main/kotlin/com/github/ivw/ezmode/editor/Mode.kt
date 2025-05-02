package com.github.ivw.ezmode.editor

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.ex.*
import com.intellij.openapi.util.*
import com.intellij.util.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import java.awt.event.*

object Mode {
  const val TYPE = "type"
  const val EZ = "ez"
  const val SELECT = "select"
  // This is not an exhaustive list.
}

class EzModeEditorData(
  val mode: String,

  /**
   * Used in select mode to track where the selection started.
   */
  val leadSelectionOffset: Int,
)

private val EZMODE_KEY = Key.create<EzModeEditorData>("ezmode")

fun Editor.getEzModeData(): EzModeEditorData? = getUserData(EZMODE_KEY)

fun Editor.getMode(): String = getEzModeData()?.mode ?: Mode.TYPE

fun Editor.setModeActually(mode: String) {
  putUserData(
    EZMODE_KEY, EzModeEditorData(
      mode = mode,
      leadSelectionOffset = selectionModel.leadSelectionOffset
    )
  )
  updateEditorColors(mode)
  modeChangedFlow.tryEmit(Unit)
  application.messageBus.syncPublisher(ModeChangeNotifier.TOPIC)
    .onChanged(mode, this)
}

/**
 * Automatically sets `select` mode if the editor has a selection.
 */
fun Editor.setMode(mode: String) {
  setModeActually(
    if (mode == Mode.EZ && selectionModel.hasSelection()) {
      if (getMode() == Mode.SELECT) {
        selectionModel.removeSelection()
        mode
      } else {
        Mode.SELECT
      }
    } else mode
  )
}

fun Editor.initEzModeData() {
  if (getEzModeData() == null) {
    setMode(service<EzModeConfigAppService>().getConfig().defaultMode ?: Mode.TYPE)
  }
}

fun initAllEditors(parentDisposable: Disposable) {
  EditorFactory.getInstance().apply {
    allEditors.forEach { it.initEzModeData() }

    // Also init any editors that are opened later:
    (eventMulticaster as? EditorEventMulticasterEx)?.addFocusChangeListener(
      object : FocusChangeListener {
        override fun focusGained(editor: Editor, event: FocusEvent) {
          editor.initEzModeData()
        }
      },
      parentDisposable,
    )
  }
}

fun Editor.clearEzModeData() {
  putUserData(EZMODE_KEY, null)
  restoreEditorColors()
}

fun clearAllEditorsEzModeData() {
  EditorFactory.getInstance().allEditors.forEach { it.clearEzModeData() }
}

/**
 * Emits whenever the mode changes.
 */
val modeChangedFlow = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  .also { it.tryEmit(Unit) }
