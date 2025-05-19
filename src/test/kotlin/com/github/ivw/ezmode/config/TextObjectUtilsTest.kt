package com.github.ivw.ezmode.config

import com.intellij.openapi.editor.*
import io.mockk.*
import org.junit.*

class TextObjectUtilsTest {
  @Test
  fun selectWordInside() {
    val chars: CharSequence = "a word!"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.offset } returns 4

    selectWord(caret, chars, around = false)
    verify { caret.setSelection(2, 6) }
  }

  @Test
  fun selectWordAroundAfter() {
    val chars: CharSequence = "a word b"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.offset } returns 4

    selectWord(caret, chars, around = true)
    verify { caret.setSelection(2, 7) }
  }

  @Test
  fun selectWordAroundBefore() {
    val chars: CharSequence = "a word!"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.offset } returns 4

    selectWord(caret, chars, around = true)
    verify { caret.setSelection(1, 6) }
  }

  @Test
  fun selectWordAroundEndOfString() {
    val chars: CharSequence = "word"
    val caret = mockk<Caret>(relaxed = true)
    every { caret.offset } returns 2

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
}
