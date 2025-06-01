package com.github.ivw.ezmode.editor

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.*
import com.intellij.openapi.util.*
import com.intellij.ui.*
import java.awt.*

val defaultPrimaryColor = JBColor(
  Color(255, 98, 0),
  Color(255, 112, 0)
)
val defaultSecondaryColor: JBColor = JBColor.BLUE

private val colorKey = EditorColors.CARET_COLOR

private val ORIGINAL_CARET_COLOR_KEY = Key.create<Color?>("ezmode.originalCaretColor")

fun Editor.updateEditorColors(mode: String, config: EzModeConfig?) {
  val originalColor: Color? = getOrCreateUserDataUnsafe(ORIGINAL_CARET_COLOR_KEY) {
    colorsScheme.getColor(colorKey)
  }
  colorsScheme.setColor(
    colorKey,
    when (mode) {
      Mode.TYPE -> originalColor
      Mode.EZ, Mode.SELECT -> config?.primaryColor ?: defaultPrimaryColor
      else -> config?.secondaryColor ?: defaultSecondaryColor
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
