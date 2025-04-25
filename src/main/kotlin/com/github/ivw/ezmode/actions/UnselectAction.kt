package com.github.ivw.ezmode.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

class UnselectAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler.ForEachCaret() {
    override fun doExecute(editor: Editor, caret: Caret, dataContext: DataContext?) {
      if (caret.hasSelection()) {
        caret.removeSelection()
      }
    }
  }
}
