package com.github.ivw.ezmode.config.textobjects

import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*

data class PairDelim(
  val openChar: Char,
  val closeChar: Char,
) : Delim {
  override fun findOpeningDelim(editor: Editor, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int? {
    val chars = editor.document.charsSequence
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

  override fun findClosingDelim(editor: Editor, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int? {
    val chars = editor.document.charsSequence
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

  override fun getMatchingDelim(fromClosingDelim: Boolean, editor: Editor, caretOffset: Int): DelimRanges? {
    val chars = editor.document.charsSequence
    if (fromClosingDelim) {
      if (caretOffset < chars.length && chars[caretOffset] != closeChar) return null
      return findOpeningDelim(editor, caretOffset, false)?.let {
        DelimRanges(TextRange(it, caretOffset))
      }
    } else {
      if (caretOffset > 0 && chars[caretOffset - 1] != openChar) return null
      return findClosingDelim(editor, caretOffset, false)?.let {
        DelimRanges(TextRange(caretOffset, it))
      }
    }
  }

  override fun toNiceString(isClosingDelim: Boolean): String =
    (if (isClosingDelim) closeChar else openChar).toString()

  companion object {
    val parentheses = PairDelim('(', ')')
    val curlyBraces = PairDelim('{', '}')
    val squareBrackets = PairDelim('[', ']')
    val angleBrackets = PairDelim('<', '>')

    val allPairs: List<PairDelim> = listOf(
      parentheses, curlyBraces, squareBrackets, angleBrackets
    )
  }
}
