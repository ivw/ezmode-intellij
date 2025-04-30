package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.popup.*

class EzModeActionGroup : DefaultActionGroup() {
  override fun actionPerformed(e: AnActionEvent) {
    JBPopupFactory.getInstance().createActionGroupPopup(
      EzModeBundle.message("group.ezmode.EzModeActionGroup.text"),
      this,
      e.dataContext,
      JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING,
      false,
    ).showInBestPositionFor(e.dataContext)
  }
}
