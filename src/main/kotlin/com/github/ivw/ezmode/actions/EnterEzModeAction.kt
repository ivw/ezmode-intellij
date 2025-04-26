package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

class EnterEzModeAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
      editor.setMode(Mode.EZ)
    }
  }
}
