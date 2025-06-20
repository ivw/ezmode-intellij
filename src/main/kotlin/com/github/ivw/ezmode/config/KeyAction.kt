package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

typealias OnComplete = () -> Unit

fun OnComplete.invokeLater() {
  ApplicationManager.getApplication().invokeLater(this)
}

/**
 * An action triggered by the EzMode key handler.
 */
abstract class KeyAction {
  abstract fun perform(e: EzModeKeyEvent, onComplete: OnComplete?)

  abstract fun toNiceString(): String

  data object Native : KeyAction() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.nativeHandler.execute(e.editor, e.char, e.dataContext)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.Native")
  }

  data class NativeOf(val keyChar: Char) : KeyAction() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.nativeHandler.execute(e.editor, keyChar, e.dataContext)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.NativeOf", keyChar)
  }

  data class Composite(val actions: List<KeyAction>) : KeyAction() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      if (actions.isEmpty()) return

      val iterator = actions.iterator()
      fun handleNext() {
        iterator.next().perform(
          e.withUpdatedMode(),
          if (iterator.hasNext()) ::handleNext else onComplete
        )
      }
      iterator.next().perform(
        e, if (iterator.hasNext()) ::handleNext else onComplete
      )
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
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.editor.project?.service<ModeService>()?.setMode(mode)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.ChangeMode", mode)
  }

  data class OfKeyChar(val keyChar: Char, val config: EzModeConfig?) : KeyAction() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.copy(char = keyChar, config = config ?: e.config).perform(onComplete)
    }

    override fun toNiceString() = "$keyChar"
  }

  data class OfMode(val mode: String, val config: EzModeConfig?) : KeyAction() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.copy(mode = mode, config = config ?: e.config).perform(onComplete)
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.KeyAction.OfMode", mode)
  }

  data object Nop : KeyAction() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      // Does nothing.
      onComplete?.invoke()
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.KeyAction.Nop")
  }
}
