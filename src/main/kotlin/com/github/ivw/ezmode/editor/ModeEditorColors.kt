package com.github.ivw.ezmode.editor

import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.*
import com.intellij.openapi.util.*
import com.intellij.ui.*
import java.awt.*

val ezModeCaretColor: JBColor = JBColor.ORANGE

private val ORIGINAL_CARET_COLOR_KEY = Key.create<Color?>("ezmode.originalCaretColor")

fun Editor.updateEditorColors(mode: String) {
  val originalColor: Color? = getOrCreateUserData(ORIGINAL_CARET_COLOR_KEY) {
    colorsScheme.getColor(EditorColors.CARET_COLOR)
  }
  colorsScheme.setColor(
    EditorColors.CARET_COLOR,
    if (mode == Mode.TYPE) {
      originalColor
    } else {
      ezModeCaretColor
    }
  )
}

fun Editor.restoreEditorColors() {
  getUserData(ORIGINAL_CARET_COLOR_KEY)?.let { originalColor ->
    colorsScheme.setColor(EditorColors.CARET_COLOR, originalColor)
    putUserData(ORIGINAL_CARET_COLOR_KEY, null)
  }
}
