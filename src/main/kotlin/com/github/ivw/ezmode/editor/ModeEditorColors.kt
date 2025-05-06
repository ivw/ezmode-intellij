package com.github.ivw.ezmode.editor

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.*
import com.intellij.openapi.util.*
import com.intellij.ui.*
import java.awt.*

val defaultEzModeCaretColor: JBColor = JBColor.ORANGE

private val colorKey = EditorColors.CARET_COLOR

private val ORIGINAL_CARET_COLOR_KEY = Key.create<Color?>("ezmode.originalCaretColor")

fun Editor.updateEditorColors(mode: String) {
  val originalColor: Color? = getOrCreateUserData(ORIGINAL_CARET_COLOR_KEY) {
    colorsScheme.getColor(colorKey)
  }
  colorsScheme.setColor(
    colorKey,
    if (mode == Mode.TYPE) {
      originalColor
    } else {
      service<EzModeConfigAppService>().getConfig().caretColor ?: defaultEzModeCaretColor
    }
  )
}

fun Editor.restoreEditorColors() {
  getUserData(ORIGINAL_CARET_COLOR_KEY)?.let { originalColor ->
    colorsScheme.setColor(colorKey, originalColor)
    putUserData(ORIGINAL_CARET_COLOR_KEY, null)
  }
}
