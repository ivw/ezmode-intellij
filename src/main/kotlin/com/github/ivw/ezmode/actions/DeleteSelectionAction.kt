package com.github.ivw.ezmode.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

class DeleteSelectionAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler.ForEachCaret() {
    override fun doExecute(editor: Editor, caret: Caret, dataContext: DataContext?) {
      if (caret.hasSelection()) {
        WriteCommandAction.runWriteCommandAction(editor.project) {
          editor.document.deleteString(caret.selectionStart, caret.selectionEnd)
          caret.removeSelection()
        }
      }
    }
  }
}
