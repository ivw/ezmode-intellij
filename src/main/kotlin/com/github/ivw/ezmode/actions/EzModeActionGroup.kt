package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.popup.*

class EzModeActionGroup : DefaultActionGroup() {
  override fun actionPerformed(e: AnActionEvent) {
    createPopup(e.dataContext).showInBestPositionFor(e.dataContext)
  }

  fun createPopup(dataContext: DataContext) =
    JBPopupFactory.getInstance().createActionGroupPopup(
      EzModeBundle.message("group.ezmode.EzModeActionGroup.text"),
      this,
      dataContext,
      JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING,
      false,
    )
}
