package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*

data class DelimPair(
  val openChar: Char,
  val closeChar: Char,
) {
  fun findOpeningDelim(chars: CharSequence, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int? {
    var oppositeDelimCount = 0
    for (i in caretOffset - 1 downTo 0) {
      val char = chars[i]
      when (char) {
        openChar -> {
          if (ignoreMatchAtCaret && i == caretOffset - 1) {
            // Ignore.
          } else if (oppositeDelimCount > 0) {
            oppositeDelimCount--
          } else {
            return i + 1
          }
        }

        closeChar -> {
          oppositeDelimCount++
        }
      }
    }
    return null
  }

  fun findClosingDelim(chars: CharSequence, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int? {
    var oppositeDelimCount = 0
    for (i in caretOffset until chars.length) {
      val char = chars[i]
      when (char) {
        closeChar -> {
          if (ignoreMatchAtCaret && i == caretOffset) {
            // Ignore.
          } else if (oppositeDelimCount > 0) {
            oppositeDelimCount--
          } else {
            return i
          }
        }

        openChar -> {
          oppositeDelimCount++
        }
      }
    }
    return null
  }
}

fun findQuoteLeft(chars: CharSequence, caretOffset: Int, quoteChar: Char, skip: Int = 1): Int? {
  for (i in caretOffset - 1 - skip downTo 0) {
    if (chars[i] == quoteChar && !isCharEscaped(chars, i)) {
      return i + 1
    }
  }
  return null
}

fun findQuoteRight(chars: CharSequence, caretOffset: Int, quoteChar: Char, skip: Int = 1): Int? {
  for (i in caretOffset + skip until chars.length) {
    if (chars[i] == quoteChar && !isCharEscaped(chars, i)) {
      return i
    }
  }
  return null
}

fun findQuoteAuto(chars: CharSequence, caretOffset: Int, quoteChar: Char, skip: Int = 1): Int? =
  if (caretOffset > skip && chars[caretOffset - skip] != quoteChar) {
    findQuoteLeft(chars, caretOffset, quoteChar)
  } else {
    findQuoteRight(chars, caretOffset, quoteChar)
  }

fun isCharEscaped(chars: CharSequence, offset: Int) =
  offset > 0 && chars[offset - 1] == '\\'

val delimPairs: List<DelimPair> = listOf(
  DelimPair('(', ')'),
  DelimPair('{', '}'),
  DelimPair('[', ']'),
  DelimPair('<', '>'),
)

val quoteChars: List<Char> = listOf('"', '\'', '`')

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
}

fun selectTextObject(caret: Caret, around: Boolean, deleteDelims: Boolean) {
  val chars = caret.editor.document.charsSequence
  if (caret.selectionStart > 0) {
    val charLeft = chars[caret.selectionStart - 1]
    val rightDelimOffset: Int? = delimPairs.firstOrNull { it.openChar == charLeft }
      ?.findClosingDelim(chars, caret.selectionStart, false)
      ?: quoteChars.firstOrNull { it == charLeft }?.let {
        findQuoteRight(chars, caret.selectionStart, it, 0)
      }
    if (rightDelimOffset != null) {
      selectRange(caret.selectionStart, rightDelimOffset, caret, around, deleteDelims)
      return
    }
  }
  if (caret.selectionEnd < chars.length) {
    val charRight = chars[caret.selectionEnd]
    val leftDelimOffset: Int? = delimPairs.firstOrNull { it.closeChar == charRight }
      ?.findOpeningDelim(chars, caret.selectionEnd, false)
      ?: quoteChars.firstOrNull { it == charRight }?.let {
        findQuoteLeft(chars, caret.selectionEnd, it, 0)
      }
    if (leftDelimOffset != null) {
      selectRange(leftDelimOffset, caret.selectionEnd, caret, around, deleteDelims)
      return
    }
  }
}

fun selectRange(start: Int, end: Int, caret: Caret, around: Boolean, deleteDelims: Boolean) {
  val aroundOffset = if (around && !deleteDelims) 1 else 0
  caret.setSelection(start - aroundOffset, end + aroundOffset)
  if (deleteDelims) {
    WriteCommandAction.runWriteCommandAction(caret.editor.project) {
      caret.editor.document.deleteString(end, end + 1)
      caret.editor.document.deleteString(start - 1, start)
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
