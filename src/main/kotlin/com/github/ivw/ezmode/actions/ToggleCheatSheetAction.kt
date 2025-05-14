package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*

class ToggleCheatSheetAction : DumbAwareAction(
  EzModeBundle.messagePointer("action.ezmode.ToggleCheatSheet.text")
) {
  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let { project ->
      ToolWindowManager.getInstance(project).getToolWindow("ezmode.cheatSheet")
        ?.toggle()
    }
  }
}

fun ToolWindow.toggle() = if (isVisible) hide() else show()
