package com.github.ivw.ezmode.editor

import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.*
import com.intellij.openapi.util.*
import java.awt.*

@Suppress("UseJBColor")
val defaultEzModeCaretColor = Color(255, 98, 0)

private val colorKey = EditorColors.CARET_COLOR

private val ORIGINAL_CARET_COLOR_KEY = Key.create<Color?>("ezmode.originalCaretColor")

fun Editor.updateEditorColors(mode: String, ezModeCaretColor: Color?) {
  val originalColor: Color? = getOrCreateUserDataUnsafe(ORIGINAL_CARET_COLOR_KEY) {
    colorsScheme.getColor(colorKey)
  }
  colorsScheme.setColor(
    colorKey,
    if (mode == Mode.TYPE) {
      originalColor
    } else {
      ezModeCaretColor ?: defaultEzModeCaretColor
    }
  )
  repaintCarets()
}

/**
 * Repaint the carets so the color change doesn't have to wait for a full caret blink.
 */
fun Editor.repaintCarets() {
  caretModel.allCarets.forEach { caret ->
    val pos = visualPositionToXY(caret.visualPosition)
    // Sometimes the caret is painted slightly to the left.
    // At a 4k resolution, the caret might be 4 pixels wide.
    contentComponent.repaint(pos.x - 2, pos.y, 6, lineHeight)
  }
}

fun Editor.restoreEditorColors() {
  getUserData(ORIGINAL_CARET_COLOR_KEY)?.let { originalColor ->
    colorsScheme.setColor(colorKey, originalColor)
    putUserData(ORIGINAL_CARET_COLOR_KEY, null)
  }
}
