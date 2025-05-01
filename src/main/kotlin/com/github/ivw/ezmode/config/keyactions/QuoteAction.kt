package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*

/**
 * An action to jump to a quote.
 */
data class QuoteAction(
  val quoteChar: Char,
) : KeyAction() {
  override fun perform(e: EzModeKeyEvent) {
    val chars = e.editor.document.charsSequence
    e.editor.caretModel.runForEachCaret { caret ->
      findQuoteAuto(chars, caret.offset, quoteChar)?.let { offset ->
        caret.removeSelection()
        caret.moveToOffset(offset)
      }
    }
  }

  override fun toNiceString() = EzModeBundle.message(
    "ezmode.QuoteAction",
    quoteChar
  )
}
