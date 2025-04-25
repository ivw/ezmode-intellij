package com.github.ivw.ezmode.keymap

import com.github.ivw.ezmode.editor.*

abstract class KeyAction {
  abstract fun perform(e: EzModeKeyEvent)

  abstract fun toNiceString(): String

  data object Native : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.nativeHandler.execute(e.editor, e.char, e.dataContext)
    }

    override fun toNiceString(): String = "Native"
  }

  data class NativeOf(val keyChar: Char) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.nativeHandler.execute(e.editor, keyChar, e.dataContext)
    }

    override fun toNiceString(): String = "Native $keyChar"
  }

  data class Composite(val actions: List<KeyAction>) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      actions.forEach { it.perform(e) }
    }

    override fun toNiceString(): String =
      actions.joinToString(separator = ", ", transform = KeyAction::toNiceString)

  }

  data class ChangeMode(val mode: String) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.editor.setMode(mode)
    }

    override fun toNiceString(): String = "Change mode to $mode"
  }

  data class OfMode(val mode: String) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.keyMap.perform(mode, e.char, e.dataContext, e.editor, e.nativeHandler)
    }

    override fun toNiceString(): String = "Action of mode $mode"
  }

  data object Nop : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      // Does nothing.
    }

    override fun toNiceString(): String = "Do nothing"
  }
}
