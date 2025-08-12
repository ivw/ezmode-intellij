package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*

typealias OnComplete = () -> Unit

fun OnComplete.invokeLater() {
  ApplicationManager.getApplication().invokeLater(this)
}

// TODO rename and redoc
/**
 * An action triggered by the EzMode key handler.
 */
abstract class KeyAction<in E> {
  abstract fun perform(e: E, onComplete: OnComplete?)

  abstract fun toNiceString(): String

  data object Native : KeyAction<EzModeKeyEvent>() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.nativeHandler.execute(e.editor, e.char, e.dataContext)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.Native")
  }

  data class NativeOf(val keyChar: Char) : KeyAction<EzModeKeyEvent>() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.nativeHandler.execute(e.editor, keyChar, e.dataContext)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.NativeOf", keyChar)
  }

  data class Composite<in E : EzModeEvent>(val actions: List<KeyAction<E>>) : KeyAction<E>() {
    override fun perform(e: E, onComplete: OnComplete?) {
      if (actions.isEmpty()) return

      val iterator = actions.iterator()
      fun handleNext() {
        iterator.next().perform(
          e, if (iterator.hasNext()) ::handleNext else onComplete
        )
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

  data class ChangeMode(val mode: String) : KeyAction<EzModeEvent>() {
    override fun perform(e: EzModeEvent, onComplete: OnComplete?) {
      e.project?.service<ModeService>()?.setMode(mode)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.KeyAction.ChangeMode", mode)
  }

  data class OfKeyChar(val keyChar: Char, val config: EzModeConfig?) : KeyAction<EzModeKeyEvent>() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.copy(char = keyChar, config = config ?: e.config).perform(onComplete)
    }

    override fun toNiceString() = "$keyChar"
  }

  data class OfMode(val mode: String, val config: EzModeConfig?) : KeyAction<EzModeKeyEvent>() {
    override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
      // TODO
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.KeyAction.OfMode", mode)
  }

  data object Nop : KeyAction<Any>() {
    override fun perform(e: Any, onComplete: OnComplete?) {
      // Does nothing.
      onComplete?.invoke()
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.KeyAction.Nop")
  }
}

abstract class EditorKeyAction : KeyAction<EzModeEvent>() {
  override fun perform(e: EzModeEvent, onComplete: OnComplete?) {
    val editor = e.editor
    if (editor == null) {
      onComplete?.invoke()
    } else {
      performWithEditor(e, editor, onComplete)
    }
  }

  abstract fun performWithEditor(e: EzModeEvent, editor: Editor, onComplete: OnComplete?)
}
