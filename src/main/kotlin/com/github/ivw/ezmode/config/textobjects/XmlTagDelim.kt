package com.github.ivw.ezmode.config.textobjects

import com.github.ivw.ezmode.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.search.*
import com.intellij.psi.util.*
import com.intellij.psi.xml.*

object XmlTagDelim : Delim {
  fun getPsiFile(editor: Editor) = editor.project?.let {
    PsiDocumentManager.getInstance(it)
  }?.getPsiFile(editor.document)

  fun getXmlTag(file: PsiFile, caretOffset: Int): XmlTag? =
    file.findElementAt(caretOffset)?.let { elementAtOffset ->
      PsiTreeUtil.getParentOfType(elementAtOffset, XmlTag::class.java)
    }

  fun getXmlTag(editor: Editor, caretOffset: Int): XmlTag? =
    getPsiFile(editor)?.let { file ->
      getXmlTag(file, caretOffset)
    }

  fun getInsideRange(tag: XmlTag): TextRange? {
    var startTagEndOffset: Int? = null
    var endTagStartOffset: Int? = null
    tag.processElements(PsiElementProcessor<PsiElement> { element ->
      when (element.node?.elementType) {
        XmlTokenType.XML_TAG_END -> {
          startTagEndOffset = element.endOffset
          true
        }

        XmlTokenType.XML_END_TAG_START -> {
          endTagStartOffset = element.startOffset
          false // Stop processing.
        }

        else -> true
      }
    }, tag)
    return if (startTagEndOffset != null && endTagStartOffset != null) {
      TextRange(startTagEndOffset, endTagStartOffset)
    } else null
  }

  override fun findOpeningDelim(
    editor: Editor,
    caretOffset: Int,
    ignoreMatchAtCaret: Boolean,
  ): Int? = getXmlTag(editor, caretOffset)?.let { tag ->
    getInsideRange(tag)?.startOffset?.let {
      if (it != caretOffset) it else {
        tag.parentTag?.let(::getInsideRange)?.startOffset
      }
    }
  }

  override fun findClosingDelim(
    editor: Editor,
    caretOffset: Int,
    ignoreMatchAtCaret: Boolean,
  ): Int? = getXmlTag(editor, caretOffset)?.let { tag ->
    getInsideRange(tag)?.endOffset?.let {
      if (it != caretOffset) it else {
        tag.parentTag?.let(::getInsideRange)?.endOffset
      }
    }
  }

  override fun getMatchingDelim(fromClosingDelim: Boolean, editor: Editor, caretOffset: Int): DelimRanges? {
    val tag = getXmlTag(editor, caretOffset) ?: return null
    val insideRange = getInsideRange(tag) ?: return null
    val isCaretAtDelim = if (fromClosingDelim) {
      caretOffset == insideRange.endOffset
    } else {
      caretOffset == insideRange.startOffset
    }
    return if (isCaretAtDelim) {
      DelimRanges(insideRange, tag.textRange)
    } else null
  }

  override fun toNiceString(isClosingDelim: Boolean): String =
    if (isClosingDelim) EzModeBundle.message("ezmode.delim.xmlTag.closing")
    else EzModeBundle.message("ezmode.delim.xmlTag.opening")
}
