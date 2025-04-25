package com.github.ivw.ezmode.keymap

import com.intellij.openapi.command.*
import com.intellij.openapi.editor.*

data class DelimPair(
  val openChar: Char,
  val closeChar: Char,
) {
  fun findOpeningDelim(chars: CharSequence, caretOffset: Int): Int? {
    var oppositeDelimCount = 0
    for (i in caretOffset - 2 downTo 0) {
      val char = chars[i]
      when (char) {
        openChar -> {
          if (oppositeDelimCount > 0) {
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

  fun findClosingDelim(chars: CharSequence, caretOffset: Int): Int? {
    var oppositeDelimCount = 0
    for (i in caretOffset + 1 until chars.length) {
      val char = chars[i]
      when (char) {
        closeChar -> {
          if (oppositeDelimCount > 0) {
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

val delimPairs: List<DelimPair> = listOf(
  DelimPair('(', ')'),
  DelimPair('{', '}'),
  DelimPair('[', ']'),
  DelimPair('<', '>'),
)

val Char.isWordChar get() = isLetterOrDigit() || this == '_'

fun selectWord(caret: Caret, chars: CharSequence, around: Boolean) {
  var start = caret.offset
  var end = caret.offset
  while (start > 0 && chars[start - 1].isWordChar) {
    start--
  }
  while (end < chars.length && chars[end].isWordChar) {
    end++
  }
  if (around) {
    // Include whitespace at the end.
    val endBeforeAround = end
    while (end < chars.length && chars[end].isWhitespace()) {
      end++
    }
    // If no whitespace at the end, add whitespace at the start.
    if (endBeforeAround == end) {
      while (start > 0 && chars[start - 1].isWhitespace()) {
        start--
      }
    }
  }
  caret.setSelection(start, end)
}

fun selectTextObject(caret: Caret, around: Boolean, deleteDelims: Boolean) {
  val chars = caret.editor.document.charsSequence
  if (caret.selectionStart > 0) {
    val charLeft = chars[caret.selectionStart - 1]
    delimPairs.firstOrNull { it.openChar == charLeft }?.let { pair ->
      pair.findClosingDelim(chars, caret.offset)
        ?.let { delim ->
          selectTextObject(caret.selectionStart, delim, caret, around, deleteDelims)
          return@selectTextObject
        }
    }
  }
  if (caret.selectionEnd < chars.length) {
    val charRight = chars[caret.selectionEnd]
    delimPairs.firstOrNull { it.closeChar == charRight }?.let { pair ->
      pair.findOpeningDelim(chars, caret.offset)
        ?.let { delim ->
          selectTextObject(delim, caret.selectionEnd, caret, around, deleteDelims)
          return@selectTextObject
        }
    }
  }
  selectWord(caret, chars, around)
}

fun selectTextObject(start: Int, end: Int, caret: Caret, around: Boolean, deleteDelims: Boolean) {
  val aroundOffset = if (around && !deleteDelims) 1 else 0
  caret.setSelection(start - aroundOffset, end + aroundOffset)
  if (deleteDelims) {
    WriteCommandAction.runWriteCommandAction(caret.editor.project) {
      caret.editor.document.deleteString(end, end + 1)
      caret.editor.document.deleteString(start - 1, start)
    }
  }
}
