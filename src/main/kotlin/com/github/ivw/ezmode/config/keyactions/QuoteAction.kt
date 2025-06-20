package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*

/**
 * An action to jump to a quote.
 */
data class QuoteAction(
  val quoteChar: Char,
) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    val chars = e.editor.document.charsSequence
    e.editor.caretModel.runForEachCaret { caret ->
      findQuoteAuto(chars, caret.offset, quoteChar)?.let { offset ->
        moveCaretWithOptionalSelection(caret, offset, e.mode)
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString() = EzModeBundle.message(
    "ezmode.QuoteAction",
    quoteChar
  )
}
