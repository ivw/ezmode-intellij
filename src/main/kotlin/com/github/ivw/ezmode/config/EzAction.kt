package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.*

typealias OnComplete = () -> Unit

fun OnComplete.invokeLater() {
  ApplicationManager.getApplication().invokeLater(this)
}

abstract class EzAction {
  abstract fun perform(e: EzModeEvent, onComplete: OnComplete?)

  abstract fun toNiceString(): String

  data object Native : KeyEzAction() {
    override fun performWithKey(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.nativeHandler.execute(e.editor, e.char, e.dataContext)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.EzAction.Native")
  }

  data class NativeOf(val keyChar: Char) : KeyEzAction() {
    override fun performWithKey(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.nativeHandler.execute(e.editor, keyChar, e.dataContext)
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.EzAction.NativeOf", keyChar)
  }

  data class Composite(val actions: List<EzAction>) : EzAction() {
    override fun perform(e: EzModeEvent, onComplete: OnComplete?) {
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

  data class ChangeMode(val mode: String) : EzAction() {
    override fun perform(e: EzModeEvent, onComplete: OnComplete?) {
      e.project?.service<ModeService>()?.setMode(
        mode.resolveVars(e, null, null)
      )
      onComplete?.invoke()
    }

    override fun toNiceString() = EzModeBundle.message("ezmode.EzAction.ChangeMode", mode)
  }

  data class OfKeyChar(val keyChar: Char, val config: EzModeConfig?) : KeyEzAction() {
    override fun performWithKey(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.copy(char = keyChar, config = config ?: e.config).perform(onComplete)
    }

    override fun toNiceString() = "$keyChar"
  }

  data class OfMode(val mode: String) : KeyEzAction() {
    private var isRunning = false

    override fun performWithKey(e: EzModeKeyEvent, onComplete: OnComplete?) {
      e.config.getBindingOrDefault(mode, e.char)?.let { keyBinding ->
        if (isRunning) {
          thisLogger().info("Recursion detected in <ofmode $mode>")
          onComplete?.invoke()
          return
        }
        isRunning = true
        try {
          keyBinding.action.perform(e, onComplete)
        } finally {
          isRunning = false
        }
      }
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.EzAction.OfMode", mode)
  }

  data object Nop : EzAction() {
    override fun perform(e: EzModeEvent, onComplete: OnComplete?) {
      // Does nothing.
      onComplete?.invoke()
    }

    override fun toNiceString(): String = EzModeBundle.message("ezmode.EzAction.Nop")
  }
}

abstract class EditorEzAction : EzAction() {
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

abstract class KeyEzAction : EzAction() {
  override fun perform(e: EzModeEvent, onComplete: OnComplete?) {
    if (e is EzModeKeyEvent) {
      performWithKey(e, onComplete)
    } else {
      onComplete?.invoke()
    }
  }

  abstract fun performWithKey(e: EzModeKeyEvent, onComplete: OnComplete?)
}
