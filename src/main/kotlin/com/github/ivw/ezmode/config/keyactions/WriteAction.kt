package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*

data class WriteAction(val text: String) : EditorKeyAction() {
  override fun performWithEditor(e: EzModeEvent, editor: Editor, onComplete: OnComplete?) {
    WriteCommandAction.runWriteCommandAction(e.project) {
      editor.caretModel.runForEachCaretIndexed { caret, caretIndex ->
        val resolvedText = text.resolveVars(e, caretIndex, caret)
        if (caret.hasSelection()) {
          editor.document.replaceString(caret.selectionStart, caret.selectionEnd, resolvedText)
          caret.removeSelection()
        } else {
          editor.document.insertString(caret.offset, resolvedText)
          caret.moveCaretRelatively(resolvedText.length, 0, false, true)
        }
      }
    }
    onComplete?.invokeLater()
  }

  override fun toNiceString(): String = EzModeBundle.message("ezmode.InsertStringAction", text)
}
