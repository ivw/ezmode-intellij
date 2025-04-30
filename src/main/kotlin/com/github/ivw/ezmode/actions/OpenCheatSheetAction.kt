package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.EzModeBundle
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*

class OpenCheatSheetAction : DumbAwareAction(
  EzModeBundle.messagePointer("action.ezmode.OpenCheatSheet.text")
) {
  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let { project ->
      ToolWindowManager.getInstance(project).getToolWindow("ezmode.cheatSheet")
        ?.show()
    }
  }
}
