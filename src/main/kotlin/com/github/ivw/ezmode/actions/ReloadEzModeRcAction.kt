package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.*
import com.intellij.openapi.project.*

class ReloadEzModeRcAction : DumbAwareAction(
  EzModeBundle.messagePointer("action.ezmode.ReloadEzModeRcAction.text")
) {
  override fun actionPerformed(e: AnActionEvent) {
    service<EzModeAppService>().loadKeymap()
  }
}
