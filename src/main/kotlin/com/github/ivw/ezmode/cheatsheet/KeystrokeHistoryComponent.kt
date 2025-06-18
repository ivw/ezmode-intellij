package com.github.ivw.ezmode.cheatsheet

import com.github.ivw.ezmode.*
import com.intellij.icons.*
import com.intellij.openapi.*
import com.intellij.openapi.components.*
import com.intellij.openapi.ide.*
import com.intellij.openapi.project.*
import com.intellij.ui.components.*
import com.intellij.ui.util.*
import java.awt.*
import java.awt.datatransfer.*
import javax.swing.*

class KeystrokeHistoryComponent(val project: Project) {
  val keystrokeHistoryService = project.service<KeystrokeHistoryService>()

  val heading = JBLabel(EzModeBundle.message("ezmode.keystrokeHistory"))

  val clearButton = IconLabelButton(AllIcons.General.Delete) {
    keystrokeHistoryService.clear()
  }
  val copyButton = IconLabelButton(AllIcons.General.Copy) {
    CopyPasteManager.getInstance().setContents(
      StringSelection(keystrokeHistoryService.getHistory())
    )
  }

  val headingPanel = JPanel().apply {
    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
    maximumSize = Dimension(Int.MAX_VALUE, preferredHeight)
    border = BorderFactory.createEmptyBorder(4, 4, 4, 4)

    add(heading)
    add(Box.createHorizontalStrut(10))
    add(clearButton)
    add(Box.createHorizontalStrut(10))
    add(copyButton)
  }

  val textField = JTextField(keystrokeHistoryService.getHistory()).apply {
    isEditable = false
    maximumSize = Dimension(Int.MAX_VALUE, preferredHeight)
  }

  fun addComponents(parent: JComponent) {
    parent.add(headingPanel)
    parent.add(textField)
  }

  fun install(parentDisposable: Disposable) {
    project.subscribeToKeystrokeHistory(parentDisposable) { history ->
      textField.text = history
    }
  }
}
