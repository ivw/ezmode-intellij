package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.keymap.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

class AroundTextObjectAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler.ForEachCaret() {
    override fun doExecute(editor: Editor, caret: Caret, dataContext: DataContext?) {
      selectTextObject(caret, true, deleteDelims = false)
    }
  }
}
