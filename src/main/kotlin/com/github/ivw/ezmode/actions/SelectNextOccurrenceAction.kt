package com.github.ivw.ezmode.actions

import com.intellij.find.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*
import com.intellij.openapi.editor.actions.*

// Copy of `com.intellij.openapi.editor.actions.SelectNextOccurrenceAction`,
// but without the having to press twice to search from the top.
// Also, this version never searches for whole words only.
class SelectNextOccurrenceAction private constructor() : EditorAction(Handler()) {
  class Handler : SelectOccurrencesActionHandler() {
    override fun isEnabledForCaret(editor: Editor, caret: Caret, dataContext: DataContext?): Boolean {
      return editor.project != null && editor.caretModel.supportsMultipleCarets()
        && !IncrementalFindAction.SEARCH_DISABLED.get(editor, false)
    }

    override fun doExecute(editor: Editor, c: Caret?, dataContext: DataContext?) {
      val caret = c ?: editor.caretModel.primaryCaret
      val wordSelectionRange = getSelectionRange(editor, caret)
      if (caret.hasSelection()) {
        val project = editor.project
        val selectedText = caret.selectedText
        if (project == null || selectedText == null) {
          return
        }
        val findManager = FindManager.getInstance(project)

        val model = getFindModel(selectedText, false)

        findManager.setSelectNextOccurrenceWasPerformed()
        findManager.findNextModel = model

        val findResult = findManager.findString(editor.document.charsSequence, caret.selectionEnd, model)
        if (findResult.isStringFound) {
          FindUtil.selectSearchResultInEditor(editor, findResult, caret.offset - caret.selectionStart)
        } else {
          // Search from the top.
          val findResult = findManager.findString(editor.document.charsSequence, 0, model)
          if (findResult.isStringFound) {
            FindUtil.selectSearchResultInEditor(editor, findResult, caret.offset - caret.selectionStart)
          }
        }
      } else {
        if (wordSelectionRange == null) {
          return
        }
        setSelection(editor, caret, wordSelectionRange)
      }
      editor.scrollingModel.scrollToCaret(ScrollType.RELATIVE)
    }
  }
}
