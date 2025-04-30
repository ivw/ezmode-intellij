package com.github.ivw.ezmode.cheatsheet

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.editor.*
import com.github.ivw.ezmode.keymap.*
import com.intellij.openapi.application.*
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
    val keyMapService = service<EzModeKeyMapAppService>()
    var currentMode = Mode.EZ

    val textArea = JTextArea().apply {
      isEditable = false
      lineWrap = true
      wrapStyleWord = true
      font = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)
      margin = JBUI.insets(5)
    }

    fun updateText() {
      textArea.text = getText(currentMode, keyMapService.getKeyMap())
    }
    updateText()

    val component = JBScrollPane(textArea)
    val content = ContentFactory.getInstance().createContent(
      component,
      null,
      true,
    )
    toolWindow.contentManager.addContent(content)

    keyMapService.subscribeToKeyMap(content) { _ ->
      updateText()
    }

    ApplicationManager.getApplication().subscribeToFocusedEditorModeChange(
      content,
    ) { mode, editor ->
      if (currentMode != mode) {
        currentMode = mode
        updateText()
      }
    }
  }

  fun getText(mode: String, keyMap: EzModeKeyMap): String =
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
      keyMap.values.forEach { keyBinding ->
        if (keyBinding.mode == mode) {
          append(keyBinding.keyChar ?: EzModeBundle.message("ezmode.EzModeKeyMap.defaultAction"))
          append(": ")
          appendLine(keyBinding.action.toNiceString())
        }
      }
    }.toString()
}
