package com.github.ivw.ezmode.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler

class ToggleCaseAction : EditorAction(Handler()) {
  class Handler : EditorWriteActionHandler.ForEachCaret() {
    override fun executeWriteAction(editor: Editor, caret: Caret, dataContext: DataContext?) {
      if (caret.hasSelection()) {
        val newText = caret.selectedText!!.toCharArray().joinToString("") { c -> c.toggleCase() }
        editor.document.replaceString(caret.selectionStart, caret.selectionEnd, newText)
      } else {
        val chars = editor.document.charsSequence
        if (caret.offset < chars.length) {
          val newChar = chars[caret.offset].toggleCase()
          editor.document.replaceString(caret.offset, caret.offset + 1, newChar)
        }
      }
    }
  }
}

fun Char.toggleCase() = if (isUpperCase()) lowercase() else uppercase()
