package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.*
import com.github.ivw.ezmode.config.textobjects.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.editor.*

/**
 * An action to jump to an opening/closing delimiter such as { or }.
 */
data class PairOpenCloseAction(
  val findClosingDelim: Boolean,
  val delims: List<Delim>,
) : EditorKeyAction() {
  override fun performWithEditor(e: EzModeEvent, editor: Editor, onComplete: OnComplete?) {
    editor.caretModel.runForEachCaret { caret ->
      delims.firstNotNullOfOrNull { delim ->
        delim.findDelim(findClosingDelim, editor, caret.offset, true)
      }?.let {
        moveCaretWithOptionalSelection(caret, it, editor.getMode())
      }
    }
    onComplete?.invoke()
  }

  override fun toNiceString() = EzModeBundle.message(
    "ezmode.PairOpenCloseAction",
    delims.joinToString { it.toNiceString(findClosingDelim) }
  )
}
