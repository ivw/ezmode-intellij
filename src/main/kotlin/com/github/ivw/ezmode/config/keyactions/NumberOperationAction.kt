package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.command.*

class NumberOperationAction(val operationId: String) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    WriteCommandAction.runWriteCommandAction(e.project) {
      e.editor.caretModel.runForEachCaret { caret ->
        if (caret.hasSelection()) {
          caret.selectionRange
        } else {
          findNumber(e.editor.document.charsSequence, caret.offset)
        }?.let { range ->
          e.editor.document.getText(range).toIntOrNull()?.let { number ->
            when (operationId) {
              "+" -> number + 1
              "-" -> number - 1
              "*" -> number * 2
              "/" -> number / 2
              else -> null
            }
          }?.let { newNumber ->
            e.editor.document.replaceString(range.startOffset, range.endOffset, newNumber.toString())
          }
        }
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString(): String =
    EzModeBundle.message("ezmode.NumberOperationAction.$operationId")
}
