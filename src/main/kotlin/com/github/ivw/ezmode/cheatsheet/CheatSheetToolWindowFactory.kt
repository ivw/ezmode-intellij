package com.github.ivw.ezmode.cheatsheet

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.colors.*
import com.intellij.openapi.keymap.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*
import com.intellij.ui.components.*
import com.intellij.ui.content.*
import com.intellij.util.ui.*
import javax.swing.*

class CheatSheetToolWindowFactory : ToolWindowFactory, DumbAware {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val configService = service<EzModeConfigAppService>()
    val modeService = project.service<ModeService>()
    var currentMode = modeService.getMode()

    val textArea = JTextArea().apply {
      isEditable = false
      lineWrap = true
      wrapStyleWord = true
      font = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)
      margin = JBUI.insets(5)
    }

    val component = JBScrollPane(textArea)
    val content = ContentFactory.getInstance().createContent(
      component, null, true,
    )
    toolWindow.contentManager.addContent(content)

    fun updateText() {
      textArea.text = getText(currentMode, configService.getConfig())
      textArea.caretPosition = 0
    }
    updateText()
    configService.subscribeToConfig(content) { _ ->
      updateText()
    }
    project.subscribeToFocusOrModeChange(content) { mode, _ ->
      if (currentMode != mode) {
        currentMode = mode
        updateText()
      }
    }
  }

  fun getText(mode: String, config: EzModeConfig): String =
    buildString {
      appendLine(EzModeBundle.getMessage("ezmode.CheatSheet.mode", mode))
      if (mode == Mode.TYPE) {
        KeymapManager.getInstance().activeKeymap.getShortcuts("ezmode.EnterEzMode")
          .firstOrNull()?.let { shortcut ->
            appendLine(
              EzModeBundle.getMessage(
                "ezmode.CheatSheet.enterEzModeHint",
                KeymapUtil.getShortcutText(shortcut)
              )
            )
          }
      }
      appendLine()
      config.getMode(mode)?.let { modeBindings ->
        fun KeyBinding.appendKey(label: String) {
          append(label)
          append(": ")
          appendLine(action.toNiceString())
        }

        modeBindings.keyBindings[null]?.appendKey(EzModeBundle.message("ezmode.EzModeKeyMap.defaultAction"))
        keyboardOrder.forEach { char ->
          modeBindings.keyBindings[char]?.appendKey(char.toString())
        }
        modeBindings.keyBindings[' ']?.appendKey(EzModeBundle.message("ezmode.EzModeKeyMap.space"))
      }
    }
}

const val keyboardOrder =
  """`~1!2@3#4$5%6^7&8*9(0)-_=+qQwWeErRtTyYuUiIoOpP[{]}\|aAsSdDfFgGhHjJkKlL;:'"zZxXcCvVbBnNmM,<.>/?"""
