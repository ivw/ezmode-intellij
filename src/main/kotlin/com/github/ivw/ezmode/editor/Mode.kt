package com.github.ivw.ezmode.editor

import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*

object Mode {
  const val TYPE = "type"
  const val EZ = "ez"
  const val SELECT = "select"
  const val VCS = "vcs"
}

class EzModeEditorData(
  val mode: String,
  val leadSelectionOffset: Int,
)

private val EZMODE_KEY = Key.create<EzModeEditorData>("ezmode")

fun Editor.getEzModeData(): EzModeEditorData? = getUserData(EZMODE_KEY)

fun Editor.getMode(): String = getEzModeData()?.mode ?: Mode.TYPE

fun Editor.setMode(mode: String) = putUserData(
  EZMODE_KEY, EzModeEditorData(
    mode,
    leadSelectionOffset = selectionModel.leadSelectionOffset
  )
)
