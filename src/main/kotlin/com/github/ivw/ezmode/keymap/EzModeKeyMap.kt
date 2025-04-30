package com.github.ivw.ezmode.keymap

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.openapi.project.*

class EzModeKeyEvent(
  val keyMap: EzModeKeyMap,
  val mode: String,
  val char: Char,
  val dataContext: DataContext,
  val editor: Editor,
  val nativeHandler: TypedActionHandler,
) {
  val project: Project? get() = editor.project
}

typealias EzModeKeyMap = Map<String, KeyBinding>
typealias MutableEzModeKeyMap = LinkedHashMap<String, KeyBinding>

private fun getHashKey(mode: String, keyChar: Char?) = "${mode}:${keyChar}"

fun EzModeKeyMap.getBindingOrDefault(mode: String, char: Char): KeyBinding? =
  this[getHashKey(mode, char)] ?: this[getHashKey(mode, null)]

fun MutableEzModeKeyMap.addBinding(binding: KeyBinding) {
  put(getHashKey(binding.mode, binding.keyChar), binding)
}

fun MutableEzModeKeyMap.copy() = MutableEzModeKeyMap(this)

fun EzModeKeyMap.perform(
  mode: String,
  char: Char,
  dataContext: DataContext,
  editor: Editor,
  nativeHandler: TypedActionHandler,
) {
  getBindingOrDefault(mode, char)?.action?.perform(
    EzModeKeyEvent(
      this, mode, char, dataContext, editor, nativeHandler
    )
  )
}

data class KeyBinding(
  val mode: String,

  /**
   * Null denotes the default key action.
   */
  val keyChar: Char?,

  val action: KeyAction,
)
