package com.github.ivw.ezmode.editor

import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*
import com.intellij.util.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

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

fun Editor.setMode(mode: String) {
  val modeBasedOnSelection = if (mode == Mode.EZ && selectionModel.hasSelection()) {
    Mode.SELECT
  } else mode
  putUserData(
    EZMODE_KEY, EzModeEditorData(
      mode = modeBasedOnSelection,
      leadSelectionOffset = selectionModel.leadSelectionOffset
    )
  )
  updateEditorColors(modeBasedOnSelection)
  modeChangedFlow.tryEmit(Unit)
  application.messageBus.syncPublisher(ModeChangeNotifier.TOPIC)
    .onChanged(mode, this)
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
