package com.github.ivw.ezmode.editor

import com.intellij.openapi.editor.event.*

/**
 * Changes to selection mode when something is selected.
 */
object ModeSelectionListener : SelectionListener {
  override fun selectionChanged(e: SelectionEvent) {
    e.editor.getEzModeData()?.let { ezModeData ->
      if (ezModeData.mode == Mode.SELECT) {
        if (e.newRange.isEmpty && e.newRange.startOffset != ezModeData.leadSelectionOffset) {
          e.editor.setModeActually(Mode.EZ)
        }
      } else if (ezModeData.mode != Mode.TYPE) {
        if (!e.newRange.isEmpty) {
          e.editor.setModeActually(Mode.SELECT)
        }
      }
    }
  }
}
