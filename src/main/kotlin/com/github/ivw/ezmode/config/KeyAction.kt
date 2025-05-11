package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.components.service

/**
 * An action triggered by the EzMode key handler.
 */
abstract class KeyAction {
  abstract fun perform(e: EzModeKeyEvent)

  abstract fun toNiceString(): String

  data object Native : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.nativeHandler.execute(e.editor, e.char, e.dataContext)
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.Native")
  }

  data class NativeOf(val keyChar: Char) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.nativeHandler.execute(e.editor, keyChar, e.dataContext)
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.NativeOf", keyChar)
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
      e.editor.project?.service<ModeService>()?.setMode(mode)
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.ChangeMode", mode)
  }

  data class OfMode(val mode: String) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.config.performKeyAction(mode, e.char, e.dataContext, e.editor, e.nativeHandler)
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.KeyAction.OfMode", mode)
  }

  data object Nop : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      // Does nothing.
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.KeyAction.Nop")
  }
}
