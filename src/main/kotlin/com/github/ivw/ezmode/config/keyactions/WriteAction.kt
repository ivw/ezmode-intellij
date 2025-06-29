package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.command.*

data class WriteAction(val text: String) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    WriteCommandAction.runWriteCommandAction(e.project) {
      e.editor.caretModel.runForEachCaret { caret ->
        if (caret.hasSelection()) {
          e.editor.document.replaceString(caret.selectionStart, caret.selectionEnd, text)
          caret.removeSelection()
        } else {
          e.editor.document.insertString(caret.offset, text)
          caret.moveCaretRelatively(text.length, 0, false, true)
        }
      }
    }
    onComplete?.invokeLater()
  }

  override fun toNiceString(): String = EzModeBundle.message("ezmode.InsertStringAction", text)
}
