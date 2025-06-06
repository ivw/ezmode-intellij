package com.github.ivw.ezmode.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.openapi.editor.actions.*

class SelectLineAction : EditorAction(Handler()) {
  class Handler : EditorActionHandler.ForEachCaret() {
    override fun doExecute(editor: Editor, caret: Caret, dataContext: DataContext?) {
      val start = editor.visualPositionToOffset(caret.selectionStartPosition.line.let { line ->
        val i = EditorActionUtil.findFirstNonSpaceColumnOnTheLine(editor, line)
        VisualPosition(line, if (i == -1) 0 else i)
      })
      val end = editor.visualPositionToOffset(caret.selectionEndPosition.line.let { line ->
        VisualPosition(line, Integer.MAX_VALUE)
      })
      caret.moveToOffset(end)
      caret.setSelection(start, end)
    }
  }
}
