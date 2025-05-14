package com.github.ivw.ezmode.actions

import com.intellij.ide.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.*

class OpenEzModeGithubAction : DumbAwareAction() {
  override fun actionPerformed(e: AnActionEvent) {
    BrowserUtil.open("https://github.com/ivw/ezmode-intellij")
  }
}
