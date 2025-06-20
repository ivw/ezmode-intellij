package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*

/**
 * An action to jump to an opening/closing delimiter such as { or }.
 */
data class PairOpenCloseAction(
  val isTargetOpen: Boolean,
  val pair: DelimPair,
) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    val chars = e.editor.document.charsSequence
    e.editor.caretModel.runForEachCaret { caret ->
      val delim = if (isTargetOpen) {
        pair.findOpeningDelim(chars, caret.offset, true)
      } else {
        pair.findClosingDelim(chars, caret.offset, true)
      }
      delim?.let {
        moveCaretWithOptionalSelection(caret, it, e.mode)
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString() = EzModeBundle.message(
    "ezmode.PairOpenCloseAction",
    if (isTargetOpen) pair.openChar else pair.closeChar
  )
}
