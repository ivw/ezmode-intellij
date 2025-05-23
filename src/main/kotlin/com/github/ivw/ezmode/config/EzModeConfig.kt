package com.github.ivw.ezmode.config

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.openapi.project.*
import java.awt.*

typealias MutableEzModeKeyMap = LinkedHashMap<ModeAndChar, KeyBinding>

class EzModeConfig(
  val keyMap: MutableEzModeKeyMap = MutableEzModeKeyMap(),
  val vars: LinkedHashMap<String, String> = LinkedHashMap(),
) {
  val defaultMode: String? get() = vars["defaultmode"]

  val caretColor: Color? by lazy {
    vars["caretcolor"]?.let {
      try {
        Color.decode(it)
      } catch (t: Throwable) {
        thisLogger().info(t)
        null
      }
    }
  }

  fun performKeyAction(
    mode: String,
    char: Char,
    dataContext: DataContext,
    editor: Editor,
    nativeHandler: TypedActionHandler,
  ) {
    keyMap.getBindingOrDefault(mode, char)?.action?.perform(
      EzModeKeyEvent(
        this, mode, char, dataContext, editor, nativeHandler
      )
    )
  }

  fun copy() = EzModeConfig(
    MutableEzModeKeyMap(keyMap),
    LinkedHashMap(vars),
  )
}

data class ModeAndChar(val mode: String, val keyChar: Char?)

fun MutableEzModeKeyMap.getBindingOrDefault(mode: String, char: Char): KeyBinding? =
  this[ModeAndChar(mode, char)] ?: this[ModeAndChar(mode, null)]

fun MutableEzModeKeyMap.addBinding(binding: KeyBinding) {
  put(ModeAndChar(binding.mode, binding.keyChar), binding)
}

data class KeyBinding(
  val mode: String,

  /**
   * Null denotes the default key action.
   */
  val keyChar: Char?,

  val action: KeyAction,
)

class EzModeKeyEvent(
  val config: EzModeConfig,
  val mode: String,
  val char: Char,
  val dataContext: DataContext,
  val editor: Editor,
  val nativeHandler: TypedActionHandler,
) {
  val project: Project? get() = editor.project
}
