package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.openapi.project.*
import java.awt.*

class ModeBindings(
  val name: String,
  val keyBindings: MutableMap<Char?, KeyBinding> = HashMap(),
) {
  fun getBindingOrDefault(char: Char): KeyBinding? =
    keyBindings[char] ?: keyBindings[null]

  fun copy() = ModeBindings(name, HashMap(keyBindings))
}

const val DEFAULT_MODES_CAPACITY = 8

class EzModeConfig(
  val modes: MutableList<ModeBindings> = ArrayList(DEFAULT_MODES_CAPACITY),
  val vars: LinkedHashMap<String, String> = LinkedHashMap(),
) {
  val defaultMode: String? get() = vars["defaultmode"]

  val primaryColor: Color? by lazy {
    vars["primarycolor"]?.let { EzModeRcParser.parseColor(it) }
  }

  val secondaryColor: Color? by lazy {
    vars["secondarycolor"]?.let { EzModeRcParser.parseColor(it) }
  }

  fun copy() = EzModeConfig(
    modes.mapTo(ArrayList(DEFAULT_MODES_CAPACITY)) { it.copy() },
    LinkedHashMap(vars),
  )

  fun getMode(name: String): ModeBindings? =
    modes.firstOrNull { it.name == name }

  fun getOrAddMode(name: String): ModeBindings =
    getMode(name) ?: ModeBindings(name).also { modes.add(it) }

  fun addBinding(mode: String, binding: KeyBinding) {
    getOrAddMode(mode).keyBindings[binding.keyChar] = binding
  }

  fun getBindingOrDefault(mode: String, char: Char): KeyBinding? =
    getMode(mode)?.getBindingOrDefault(char)
}

data class KeyBinding(
  /**
   * Null denotes the default key action.
   */
  val keyChar: Char?,

  val action: EzAction,
)

sealed interface EzModeEvent {
  val project: Project?
  val config: EzModeConfig
  val dataContext: DataContext
  val editor: Editor?
}

data class EzModeProjectEvent(
  override val project: Project,
  override val config: EzModeConfig,
) : EzModeEvent {
  override val dataContext: DataContext
    get() = TODO("Not yet implemented")

  override val editor: Editor?
    get() = project.service<ModeService>().focusedEditor
}

data class EzModeKeyEvent(
  override val config: EzModeConfig,
  val char: Char,
  override val dataContext: DataContext,
  override val editor: Editor,
  val nativeHandler: TypedActionHandler,
) : EzModeEvent {
  override val project: Project? get() = editor.project

  fun getEzAction(): EzAction? =
    config.getBindingOrDefault(editor.getMode(), char)?.action

  fun perform(onComplete: OnComplete?) {
    getEzAction()?.perform(this, onComplete)
  }
}
