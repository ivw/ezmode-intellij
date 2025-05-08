package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

class EnterEzModeAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
      editor.project?.service<ModeService>()?.setMode(Mode.EZ)
    }

    override fun isEnabledForCaret(
      editor: Editor,
      caret: Caret,
      dataContext: DataContext?,
    ): Boolean = editor.getMode() != Mode.EZ
  }
}
