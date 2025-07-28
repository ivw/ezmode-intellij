package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.github.ivw.ezmode.config.textobjects.*

/**
 * An action to jump to an opening/closing delimiter such as { or }.
 */
data class PairOpenCloseAction(
  val findClosingDelim: Boolean,
  val delims: List<Delim>,
) : KeyAction() {
  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    e.editor.caretModel.runForEachCaret { caret ->
      delims.firstNotNullOfOrNull { delim ->
        delim.findDelim(findClosingDelim, e.editor, caret.offset, true)
      }?.let {
        moveCaretWithOptionalSelection(caret, it, e.mode)
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString() = EzModeBundle.message(
    "ezmode.PairOpenCloseAction",
    delims.joinToString { it.toNiceString(findClosingDelim) }
  )
}
