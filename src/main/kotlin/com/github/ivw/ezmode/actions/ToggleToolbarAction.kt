package com.github.ivw.ezmode.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*

abstract class ToggleToolbarAction : DumbAwareAction() {
  /**
   * @see ToolWindowId
   */
  abstract val id: String

  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let { project ->
      ToolWindowManager.getInstance(project).getToolWindow(id)?.toggle()
    }
  }

  class Project : ToggleToolbarAction() {
    override val id: String get() = "Project"
  }

  class Commit : ToggleToolbarAction() {
    override val id: String get() = "Commit"
  }

  class Terminal : ToggleToolbarAction() {
    override val id: String get() = "Terminal"
  }

  class CheatSheet : ToggleToolbarAction() {
    override val id: String get() = "ezmode.cheatSheet"
  }
}

fun ToolWindow.toggle() = if (isVisible) hide() else show()
