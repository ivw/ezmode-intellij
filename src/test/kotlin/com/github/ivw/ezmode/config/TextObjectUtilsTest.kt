package com.github.ivw.ezmode.config

import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*
import io.kotest.matchers.*
import io.mockk.*
import org.junit.*

class TextObjectUtilsTest {
  @Test
  fun findOpeningOrClosingDelim() {
    val chars: CharSequence = "a(b(c[d]f{g}))"
    DelimPair('(', ')').apply {
      findOpeningDelim(chars, 4, true)
        .shouldBe(2)
      findOpeningDelim(chars, 13, true)
        .shouldBe(2)
      findClosingDelim(chars, 12, true)
        .shouldBe(13)
      findClosingDelim(chars, 3, true)
        .shouldBe(13)
    }
  }

  @Test
  fun selectWordInside() {
    val chars: CharSequence = "a word!"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.selectionStart } returns 4
    every { caret.selectionEnd } returns 4

    selectWord(caret, chars, around = false)
    verify { caret.setSelection(2, 6) }
  }

  @Test
  fun selectWordAroundAfter() {
    val chars: CharSequence = "a word b"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.selectionStart } returns 4
    every { caret.selectionEnd } returns 4

    selectWord(caret, chars, around = true)
    verify { caret.setSelection(2, 7) }
  }

  @Test
  fun selectWordAroundBefore() {
    val chars: CharSequence = "a word!"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.selectionStart } returns 4
    every { caret.selectionEnd } returns 4

    selectWord(caret, chars, around = true)
    verify { caret.setSelection(1, 6) }
  }

  @Test
  fun selectWordAroundEndOfString() {
    val chars: CharSequence = "word"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.selectionStart } returns 2
    every { caret.selectionEnd } returns 2

    selectWord(caret, chars, around = true)
    verify { caret.setSelection(0, 4) }
  }

  @Test
  fun selectTextObjectInsideDelimPair() {
    val chars: CharSequence = "(aa bb)"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.editor.document.charsSequence } returns chars
    every { caret.offset } returns 1
    every { caret.selectionStart } returns 1
    every { caret.selectionEnd } returns 1
    mockkStatic(::selectTextObject)

    selectTextObject(caret, around = false, deleteDelims = false)
    verify { selectRange(1, 6, caret, around = false, deleteDelims = false) }
  }

  @Test
  fun selectTextObjectInsideEmptyDelimPair() {
    val chars: CharSequence = "()"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.editor.document.charsSequence } returns chars
    every { caret.offset } returns 1
    every { caret.selectionStart } returns 1
    every { caret.selectionEnd } returns 1
    mockkStatic(::selectTextObject)

    selectTextObject(caret, around = false, deleteDelims = false)
    verify { selectRange(1, 1, caret, around = false, deleteDelims = false) }
  }

  @Test
  fun selectTextObjectAroundEmptyDelimPair() {
    val chars: CharSequence = "()"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.editor.document.charsSequence } returns chars
    every { caret.offset } returns 1
    every { caret.selectionStart } returns 1
    every { caret.selectionEnd } returns 1
    mockkStatic(::selectTextObject)

    selectTextObject(caret, around = true, deleteDelims = false)
    verify { selectRange(1, 1, caret, around = true, deleteDelims = false) }
  }

  @Test
  fun selectTextObjectInsideQuote() {
    val chars: CharSequence = "'aa bb'"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.editor.document.charsSequence } returns chars
    every { caret.offset } returns 1
    every { caret.selectionStart } returns 1
    every { caret.selectionEnd } returns 1
    mockkStatic(::selectTextObject)

    selectTextObject(caret, around = false, deleteDelims = false)
    verify { selectRange(1, 6, caret, around = false, deleteDelims = false) }
  }

  @Test
  fun selectTextObjectInsideEmptyQuote() {
    val chars: CharSequence = "''"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.editor.document.charsSequence } returns chars
    every { caret.offset } returns 1
    every { caret.selectionStart } returns 1
    every { caret.selectionEnd } returns 1
    mockkStatic(::selectTextObject)

    selectTextObject(caret, around = false, deleteDelims = false)
    verify { selectRange(1, 1, caret, around = false, deleteDelims = false) }
  }

  @Test
  fun selectTextObjectWithSelectionRange() {
    val chars: CharSequence = "(foo())"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.editor.document.charsSequence } returns chars
    every { caret.offset } returns 5
    every { caret.selectionStart } returns 4
    every { caret.selectionEnd } returns 6
    mockkStatic(::selectTextObject)

    selectTextObject(caret, around = false, deleteDelims = false)
    verify { selectRange(1, 6, caret, around = false, deleteDelims = false) }
  }

  @Test
  fun getTextRangeOfInt() {
    val chars: CharSequence = "--123-"
    val numberTextRange = TextRange(1, 5)
    getTextRangeOfInt(chars, 0).shouldBe(null)
    getTextRangeOfInt(chars, 1).shouldBe(numberTextRange)
    getTextRangeOfInt(chars, 2).shouldBe(numberTextRange)
    getTextRangeOfInt(chars, 3).shouldBe(numberTextRange)
    getTextRangeOfInt(chars, 4).shouldBe(numberTextRange)
    getTextRangeOfInt(chars, 5).shouldBe(numberTextRange)
    getTextRangeOfInt(chars, 6).shouldBe(null)

    getTextRangeOfInt("-1-1", 2).shouldBe(
      TextRange(2, 4)
    )

    getTextRangeOfInt("-", 0).shouldBe(null)
  }
}
