package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

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
      if (actions.isEmpty()) return

      val iterator = actions.iterator()

      // Perform the first action directly.
      iterator.next().perform(e)

      // Schedule the next action (recursively)
      val app = ApplicationManager.getApplication()
      fun handleNext() {
        if (iterator.hasNext()) {
          app.invokeLater {
            iterator.next().perform(e.withUpdatedMode())
            handleNext()
          }
        }
      }
      handleNext()
    }

    override fun toNiceString(): String = if (actions.isEmpty()) "" else buildString {
      val iterator = actions.iterator()

      var lastAction = iterator.next()
      append(lastAction.toNiceString())
      while (iterator.hasNext()) {
        val action = iterator.next()
        if (lastAction is OfKeyChar && action is OfKeyChar) {
          // Do not add separator between two `OfKeyChar` actions.
        } else {
          append(", ")
        }
        append(action.toNiceString())
        lastAction = action
      }
    }
  }

  data class ChangeMode(val mode: String) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.editor.project?.service<ModeService>()?.setMode(mode)
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.ChangeMode", mode)
  }

  data class OfKeyChar(val keyChar: Char, val config: EzModeConfig?) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.copy(char = keyChar, config = config ?: e.config).perform()
    }

    override fun toNiceString() = "$keyChar"
  }

  data class OfMode(val mode: String, val config: EzModeConfig?) : KeyAction() {
    override fun perform(e: EzModeKeyEvent) {
      e.copy(mode = mode, config = config ?: e.config).perform()
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
