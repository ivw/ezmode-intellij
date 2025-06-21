package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.command.*

data class WriteVarAction(val varName: String) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    WriteCommandAction.runWriteCommandAction(e.project) {
      var caretIndex = 0
      e.editor.caretModel.runForEachCaret { caret ->
        val text: String? = when (varName) {
          "caretindex" -> caretIndex.toString()
          "line" -> (caret.logicalPosition.line + 1).toString()
          "column" -> (caret.logicalPosition.column + 1).toString()
          "filename" -> e.editor.virtualFile.nameWithoutExtension
          else -> e.config.vars[varName]
        }
        if (text != null) {
          if (caret.hasSelection()) {
            e.editor.document.replaceString(caret.selectionStart, caret.selectionEnd, text)
            caret.removeSelection()
          } else {
            e.editor.document.insertString(caret.offset, text)
            caret.moveCaretRelatively(text.length, 0, false, true)
          }
          caretIndex++
        }
      }
    }
    onComplete?.invokeLater()
  }

  override fun toNiceString(): String = EzModeBundle.message("ezmode.WriteVarAction", varName)
}
