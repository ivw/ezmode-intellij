package com.github.ivw.ezmode.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

class SelectLineAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler.ForEachCaret() {
    override fun doExecute(editor: Editor, caret: Caret, dataContext: DataContext?) {
      val start = editor.visualPositionToOffset(VisualPosition(caret.selectionStartPosition.line, 0))
      val end = editor.visualPositionToOffset(caret.selectionEndPosition.line.let { line ->
        VisualPosition(line, editor.document.getLineEndOffset(line) - 1)
      })
      caret.moveToOffset(start)
      caret.setSelection(start, end)
    }
  }
}
