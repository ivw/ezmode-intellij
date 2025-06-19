package com.github.ivw.ezmode.listeners

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import org.acejump.action.*
import org.acejump.session.*

class AceJumpActionListener : AnActionListener {
  /**
   * After an AceJump action, we want to update the selection (if select mode),
   * and recalculate the editor colors.
   */
  override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
    if (action is AceAction) {
      CommonDataKeys.EDITOR.getData(event.dataContext)?.let { editor ->
        editor.project?.service<ModeService>()?.apply {
          val mode = getMode(editor)
          val offsetBeforeJumping = editor.selectionModel.leadSelectionOffset
          SessionManager[editor]?.addAceJumpListener(object : AceJumpListener {
            override fun finished(mark: String?, query: String?) {
              if (focusedEditor == editor) {
                if (mode == Mode.SELECT) {
                  editor.selectionModel.setSelection(offsetBeforeJumping, editor.caretModel.offset)
                }

                ApplicationManager.getApplication().invokeLater {
                  editor.updateEditorColors(mode, config)
                }
              }
            }
          })
        }
      }
    }
  }
}
