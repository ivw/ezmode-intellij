package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

class SelectWordAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler.ForEachCaret() {
    override fun doExecute(editor: Editor, caret: Caret, dataContext: DataContext?) {
      selectWord(caret, editor.document.charsSequence, false)
    }
  }
}
