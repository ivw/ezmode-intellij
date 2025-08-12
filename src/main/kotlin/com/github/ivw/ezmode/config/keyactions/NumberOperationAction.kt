package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*

class NumberOperationAction(val operationId: String) : EditorKeyAction() {
  override fun performWithEditor(e: EzModeEvent, editor: Editor, onComplete: OnComplete?) {
    WriteCommandAction.runWriteCommandAction(e.project) {
      editor.caretModel.runForEachCaret { caret ->
        if (caret.hasSelection()) {
          caret.selectionRange
        } else {
          getTextRangeOfInt(editor.document.charsSequence, caret.offset)
        }?.let { range ->
          editor.document.getText(range).toIntOrNull()?.let { number ->
            when (operationId) {
              "+" -> number + 1
              "-" -> number - 1
              "*" -> number * 2
              "/" -> number / 2
              else -> null
            }
          }?.let { newNumber ->
            editor.document.replaceString(range.startOffset, range.endOffset, newNumber.toString())
          }
        }
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString(): String =
    EzModeBundle.message("ezmode.NumberOperationAction.$operationId")
}
