package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.keymap.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.*
import com.intellij.openapi.vfs.*
import java.io.*

class OpenEzModeRcAction(val file: File? = null) : DumbAwareAction(
  EzModeBundle.messagePointer("action.ezmode.OpenEzModeRcAction.text")
) {
  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let { project ->
      val file = this.file ?: EzModeRcFileUtils.ensureUserRcFileExists(
        EzModeRcFileUtils.USER_IDEA_RC_NAME
      )
      LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)?.let { virtualFile ->
        FileEditorManager.getInstance(project).openFile(virtualFile, true)
      }
    }
  }
}
