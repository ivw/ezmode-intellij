package com.github.ivw.ezmode.editor

import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

object Mode {
  const val TYPE = "type"
  const val EZ = "ez"
  const val SELECT = "select"
  // This is not an exhaustive list.
}

class EzModeEditorData(
  val mode: String,
  val leadSelectionOffset: Int,
)

private val EZMODE_KEY = Key.create<EzModeEditorData>("ezmode")

fun Editor.getEzModeData(): EzModeEditorData? = getUserData(EZMODE_KEY)

fun Editor.getMode(): String = getEzModeData()?.mode ?: Mode.TYPE

fun Editor.setMode(mode: String) {
  putUserData(
    EZMODE_KEY, EzModeEditorData(
      mode = mode,
      leadSelectionOffset = selectionModel.leadSelectionOffset
    )
  )
  updateEditorColors(mode)
  modeChangedFlow.tryEmit(Unit)
}

val modeChangedFlow = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  .also { it.tryEmit(Unit) }
