package com.github.ivw.ezmode.editor

import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

object Mode {
  const val TYPE = "type"
  const val EZ = "ez"
  const val SELECT = "select"
  // This is not an exhaustive list.
}

private val SELECT_MODE_LEAD_OFFSET_KEY = Key.create<Int>("ezmode.selectModeLeadOffsetKey")

fun Editor.getSelectModeLeadOffset(): Int? =
  getUserData(SELECT_MODE_LEAD_OFFSET_KEY)

fun Editor.setSelectModeLeadOffset(
  offset: Int? = selectionModel.leadSelectionOffset,
) = putUserData(SELECT_MODE_LEAD_OFFSET_KEY, offset)

fun Editor.getMode(): String =
  project?.service<ModeService>()?.getMode(this) ?: Mode.TYPE

fun Editor.clearEzModeData() {
  setSelectModeLeadOffset(null)
  restoreEditorColors()
}

fun clearAllEditorsEzModeData() {
  EditorFactory.getInstance().allEditors.forEach { it.clearEzModeData() }
}

/**
 * Emits whenever the focused editor or its mode changes.
 */
val focusOrModeChangedFlow = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  .also { it.tryEmit(Unit) }
