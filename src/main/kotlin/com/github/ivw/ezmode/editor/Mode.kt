package com.github.ivw.ezmode.editor

import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*

object Mode {
  const val TYPE = "type"
  const val EZ = "ez"
  const val SELECT = "select"
  const val GIT = "git"
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
