package com.github.ivw.ezmode.config.ezactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.github.ivw.ezmode.config.textobjects.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.editor.*

/**
 * An action to jump to a quote.
 */
data class QuoteAction(
  val quote: QuoteDelim,
) : EditorEzAction() {
  override fun performWithEditor(e: EzModeEvent, editor: Editor, onComplete: OnComplete?) {
    editor.caretModel.runForEachCaret { caret ->
      quote.findAuto(editor, caret.offset)?.let { offset ->
        moveCaretWithOptionalSelection(caret, offset, editor.getMode())
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString() = EzModeBundle.message(
    "ezmode.QuoteAction",
    quote.char
  )
}
