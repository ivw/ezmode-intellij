package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.config.*
import com.github.ivw.ezmode.editor.*
import com.intellij.ide.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import org.acejump.action.*
import org.acejump.session.*
import java.awt.event.*
import javax.swing.*

data class IdeKeyAction(val actionId: String) : KeyAction() {
  val anAction: AnAction? by lazy {
    ActionManager.getInstance().getAction(actionId)
      .also { if (it == null) LOG.info("Action not found: $actionId") }
  }

  override fun perform(e: EzModeKeyEvent) {
    anAction?.let { anAction ->
      if (anAction is EmptyAction) {
        handleEmptyAction(anAction, e)
      } else {
        val anActionEvent = AnActionEvent.createEvent(
          anAction,
          e.dataContext,
          null,
          ActionPlaces.KEYBOARD_SHORTCUT,
          ActionUiKind.NONE,
          null,
        )

        ActionUtil.performDumbAwareUpdate(anAction, anActionEvent, false)
        if (anActionEvent.presentation.isEnabled) {
          ActionUtil.performActionDumbAwareWithCallbacks(anAction, anActionEvent)
          if (anAction is AceAction) afterAceActionPerformed(e.editor)
        }
      }
    }
  }

  /**
   * After an AceJump action, we want to update the selection (if select mode),
   * and recalculate the editor colors.
   */
  fun afterAceActionPerformed(editor: Editor) {
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
              editor.updateEditorColors(mode, ezModeCaretColor)
            }
          }
        }
      })
    }
  }

  /**
   * Some actions (e.g. NextDiff) are defined globally as an EmptyAction,
   * with the real action added locally to some context, such as a diff viewer.
   * This is a workaround that simulates the keystroke defined in the EmptyAction.
   */
  fun handleEmptyAction(anAction: EmptyAction, e: EzModeKeyEvent) {
    val keyStroke = anAction.getKeyStroke() ?: run {
      LOG.info("Action is empty and has no keyboard shortcut: $actionId")
      return
    }
    val event = KeyEvent(
      e.editor.contentComponent,
      KeyEvent.KEY_PRESSED,
      System.currentTimeMillis(),
      keyStroke.modifiers,
      keyStroke.keyCode,
      KeyEvent.CHAR_UNDEFINED
    )
    IdeEventQueue.getInstance().dispatchEvent(event)
  }

  override fun toNiceString(): String =
    anAction?.templatePresentation?.description
      ?: anAction?.templateText
      ?: actionId

  companion object {
    val LOG = logger<IdeKeyAction>()
  }
}

fun AnAction.getKeyStroke(): KeyStroke? {
  for (shortcut in shortcutSet.shortcuts) {
    if (shortcut is KeyboardShortcut && shortcut.secondKeyStroke == null) {
      return shortcut.firstKeyStroke
    }
  }
  return null
}
