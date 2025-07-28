package com.github.ivw.ezmode.config.textobjects

import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*

interface Delim {
  fun findOpeningDelim(editor: Editor, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int?
  fun findClosingDelim(editor: Editor, caretOffset: Int, ignoreMatchAtCaret: Boolean): Int?
  fun getMatchingDelim(fromClosingDelim: Boolean, editor: Editor, caretOffset: Int): DelimRanges?
  fun toNiceString(isClosingDelim: Boolean): String

  companion object {
    val allDelims by lazy { PairDelim.allPairs + QuoteDelim.allQuotes + XmlTagDelim }
  }
}

data class DelimRanges(val insideRange: TextRange, val aroundRange: TextRange) {
  constructor(insideRange: TextRange, delimLength: Int = 1) : this(
    insideRange, TextRange(
      insideRange.startOffset - delimLength,
      insideRange.endOffset + delimLength,
    )
  )
}

fun Delim.findDelim(isClosingDelim: Boolean, editor: Editor, caretOffset: Int, ignoreMatchAtCaret: Boolean) =
  if (isClosingDelim) {
    findClosingDelim(editor, caretOffset, ignoreMatchAtCaret)
  } else {
    findOpeningDelim(editor, caretOffset, ignoreMatchAtCaret)
  }
