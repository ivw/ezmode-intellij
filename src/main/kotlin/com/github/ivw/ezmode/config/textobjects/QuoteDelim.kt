package com.github.ivw.ezmode.config.textobjects

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*

data class QuoteDelim(val char: Char) : Delim {
  override fun findOpeningDelim(editor: Editor, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int? {
    val chars = editor.document.charsSequence
    for (i in caretOffset - 1 - (if (ignoreMatchAtCaret) 1 else 0) downTo 0) {
      if (chars[i] == char && !isCharEscaped(chars, i)) {
        return i + 1
      }
    }
    return null
  }

  override fun findClosingDelim(editor: Editor, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int? {
    val chars = editor.document.charsSequence
    for (i in caretOffset + (if (ignoreMatchAtCaret) 1 else 0) until chars.length) {
      if (chars[i] == char && !isCharEscaped(chars, i)) {
        return i
      }
    }
    return null
  }

  /**
   * If the caret is at the opening quote, finds the closing quote, else finds the opening quote.
   */
  fun findAuto(editor: Editor, caretOffset: Int): Int? =
    if (caretOffset > 1 && editor.document.charsSequence[caretOffset - 1] != char) {
      findOpeningDelim(editor, caretOffset, false)
    } else {
      findClosingDelim(editor, caretOffset, false)
    }

  override fun getMatchingDelim(fromClosingDelim: Boolean, editor: Editor, caretOffset: Int): DelimRanges? {
    val chars = editor.document.charsSequence
    return if (fromClosingDelim) {
      if (caretOffset < chars.length && chars[caretOffset] != char) return null
      findOpeningDelim(editor, caretOffset, false)?.let {
        DelimRanges(TextRange(it, caretOffset))
      }
    } else {
      if (caretOffset > 0 && chars[caretOffset - 1] != char) return null
      findClosingDelim(editor, caretOffset, false)?.let {
        DelimRanges(TextRange(caretOffset, it))
      }
    }
  }

  override fun toNiceString(isClosingDelim: Boolean): String =
    char.toString()

  companion object {
    val doubleQuote = QuoteDelim('"')
    val singleQuote = QuoteDelim('\'')
    val backtick = QuoteDelim('`')
    val allQuotes = listOf(doubleQuote, singleQuote, backtick)
  }
}
