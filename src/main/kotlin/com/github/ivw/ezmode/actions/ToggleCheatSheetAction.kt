package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.config.keyactions.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*

class ToggleCheatSheetAction : DumbAwareAction() {
  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let { project ->
      ToolWindowManager.getInstance(project).getToolWindow("ezmode.cheatSheet")
        ?.toggle()
    }
  }
}
