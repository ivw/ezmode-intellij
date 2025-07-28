package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.config.textobjects.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*

fun isCharEscaped(chars: CharSequence, offset: Int) =
  offset > 0 && chars[offset - 1] == '\\'

val Char.isWordChar get() = isLetterOrDigit() || this == '_'

fun selectWord(caret: Caret, chars: CharSequence, around: Boolean) {
  var start = caret.selectionStart
  var end = caret.selectionEnd
  while (start > 0 && chars[start - 1].isWordChar) {
    start--
  }
  while (end < chars.length && chars[end].isWordChar) {
    end++
  }
  if (around) {
    // Include whitespace at the end.
    val endBeforeAround = end
    while (end < chars.length && chars[end] == ' ') {
      end++
    }
    // If no whitespace at the end, add whitespace at the start.
    if (endBeforeAround == end) {
      while (start > 0 && chars[start - 1] == ' ') {
        start--
      }
    }
  }
  caret.moveToOffset(end)
  caret.setSelection(start, end)
  caret.editor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
}

fun getTextRangeOfInt(chars: CharSequence, caretOffset: Int): TextRange? {
  var end = caretOffset
  if (end + 1 < chars.length && chars[end] == '-' && chars[end + 1].isDigit()) {
    end += 2
    while (end < chars.length && chars[end].isDigit()) {
      end++
    }
    return TextRange(caretOffset, end)
  }
  while (end < chars.length && chars[end].isDigit()) {
    end++
  }

  var start = caretOffset
  while (start > 0 && chars[start - 1].isDigit()) {
    start--
  }
  if (start == end) return null
  if (start > 0 && chars[start - 1] == '-') {
    start--
  }

  return TextRange(start, end)
}

fun selectTextObject(caret: Caret, around: Boolean, deleteDelims: Boolean) {
  Delim.allDelims.forEach { delim ->
    delim.getMatchingDelim(false, caret.editor, caret.selectionStart)?.let {
      selectRange(it, caret, around, deleteDelims)
      return
    }
  }
  Delim.allDelims.forEach { delim ->
    delim.getMatchingDelim(true, caret.editor, caret.selectionEnd)?.let {
      selectRange(it, caret, around, deleteDelims)
      return
    }
  }
}

fun Caret.setSelection(textRange: TextRange) =
  setSelection(textRange.startOffset, textRange.endOffset)

fun selectRange(delimRanges: DelimRanges, caret: Caret, around: Boolean, deleteDelims: Boolean) {
  caret.setSelection(
    if (around && !deleteDelims) delimRanges.aroundRange
    else delimRanges.insideRange
  )
  caret.editor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
  if (deleteDelims) {
    WriteCommandAction.runWriteCommandAction(caret.editor.project) {
      caret.editor.document.deleteString(delimRanges.insideRange.endOffset, delimRanges.aroundRange.endOffset)
      caret.editor.document.deleteString(delimRanges.aroundRange.startOffset, delimRanges.insideRange.startOffset)
    }
  }
}

fun moveCaretWithOptionalSelection(caret: Caret, offset: Int, mode: String) {
  if (mode == Mode.SELECT) {
    caret.setSelection(caret.leadSelectionOffset, offset)
  } else {
    caret.removeSelection()
  }
  caret.moveToOffset(offset)
  caret.editor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
}
