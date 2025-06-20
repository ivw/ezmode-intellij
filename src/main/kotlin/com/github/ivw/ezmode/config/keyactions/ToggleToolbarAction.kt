package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.wm.*

class ToggleToolWindowAction(
  /**
   * @see ToolWindowId
   */
  val id: String,
) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    e.project?.let { project ->
      ToolWindowManager.getInstance(project).getToolWindow(id)?.toggle()
    }
    onComplete?.invokeLater()
  }

  override fun toNiceString(): String {
    return EzModeBundle.message("ezmode.KeyAction.ToggleToolWindow", id)
  }
}

fun ToolWindow.toggle() = if (isVisible) hide() else show()
