package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.github.ivw.ezmode.config.textobjects.*

/**
 * An action to jump to a quote.
 */
data class QuoteAction(
  val quote: QuoteDelim,
) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    e.editor.caretModel.runForEachCaret { caret ->
      quote.findAuto(e.editor, caret.offset)?.let { offset ->
        moveCaretWithOptionalSelection(caret, offset, e.mode)
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString() = EzModeBundle.message(
    "ezmode.QuoteAction",
    quote.char
  )
}
