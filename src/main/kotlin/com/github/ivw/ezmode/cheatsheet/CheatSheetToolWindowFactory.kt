package com.github.ivw.ezmode.cheatsheet

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.editor.*
import com.github.ivw.ezmode.keymap.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.colors.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*
import com.intellij.ui.components.*
import com.intellij.ui.content.*
import com.intellij.util.ui.*
import javax.swing.*

class CheatSheetToolWindowFactory : ToolWindowFactory, DumbAware {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val appService = service<EzModeAppService>()
    val editorFont = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)
    val textArea = JTextArea().apply {
      isEditable = false
      lineWrap = true
      wrapStyleWord = true
      font = editorFont
      margin = JBUI.insets(5)
    }

    fun updateText() {
      textArea.text = appService.getKeyMap().toNiceString(Mode.EZ)
    }
    updateText()

    val component = JBScrollPane(textArea)
    val content = ContentFactory.getInstance().createContent(
      component,
      null,
      false
    )
    toolWindow.contentManager.addContent(content)

    appService.subscribeToKeyMap(content) { _ ->
      updateText()
    }
  }
}
