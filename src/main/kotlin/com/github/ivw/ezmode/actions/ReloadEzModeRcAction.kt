package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.*
import com.intellij.openapi.project.*

class ReloadEzModeRcAction : DumbAwareAction(
  EzModeBundle.messagePointer("action.ezmode.ReloadEzModeRc.text")
) {
  override fun actionPerformed(e: AnActionEvent) {
    service<EzModeConfigAppService>().loadConfig()
  }
}
