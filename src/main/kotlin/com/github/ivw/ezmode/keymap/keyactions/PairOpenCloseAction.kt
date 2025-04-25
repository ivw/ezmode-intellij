package com.github.ivw.ezmode.keymap.keyactions

import com.github.ivw.ezmode.keymap.*

/**
 * An action to jump to an opening/closing delimiter such as { or }.
 */
data class PairOpenCloseAction(
  val isTargetOpen: Boolean,
  val pair: DelimPair,
) : KeyAction() {
  override fun perform(e: EzModeKeyEvent) {
    val chars = e.editor.document.charsSequence
    e.editor.caretModel.runForEachCaret { caret ->
      val delim = if (isTargetOpen) {
        pair.findOpeningDelim(chars, caret.offset)
      } else {
        pair.findClosingDelim(chars, caret.offset)
      }
      delim?.let {
        caret.removeSelection()
        caret.moveToOffset(it)
      }
    }
  }

  override fun toNiceString(): String =
    "Move caret to ${if (isTargetOpen) pair.openChar else pair.closeChar}"
}
