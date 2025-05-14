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

    fun updateText() {
      textArea.text = getText(currentMode, configService.getConfig())
    }
    updateText()

    val component = JBScrollPane(textArea)
    val content = ContentFactory.getInstance().createContent(
      component,
      null,
      true,
    )
    toolWindow.contentManager.addContent(content)

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
    StringBuilder().apply {
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
      config.keyMap.values.forEach { keyBinding ->
        if (keyBinding.mode == mode) {
          append(
            when (keyBinding.keyChar) {
              ' ' -> EzModeBundle.message("ezmode.EzModeKeyMap.space")
              null -> EzModeBundle.message("ezmode.EzModeKeyMap.defaultAction")
              else -> keyBinding.keyChar.toString()
            }
          )
          append(": ")
          appendLine(keyBinding.action.toNiceString())
        }
      }
    }.toString()
}
