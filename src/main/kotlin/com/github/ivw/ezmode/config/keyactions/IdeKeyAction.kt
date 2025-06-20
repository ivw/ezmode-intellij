package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.config.*
import com.intellij.ide.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.*
import com.intellij.openapi.diagnostic.*
import java.awt.event.*
import javax.swing.*

const val EZMODE_ACTION_PLACE = "ezmode"

data class IdeKeyAction(val actionId: String) : KeyAction() {
  val anAction: AnAction? by lazy {
    ActionManager.getInstance().getAction(actionId)
      .also { if (it == null) LOG.info("Action not found: $actionId") }
  }

  override fun perform(e: EzModeKeyEvent, onComplete: OnComplete?) {
    anAction?.let { anAction ->
      if (anAction is EmptyAction) {
        handleEmptyAction(anAction, e)
      } else {
        anAction.performActionIfEnabled(e.dataContext, EZMODE_ACTION_PLACE)
      }
    }
    onComplete?.invokeLater()
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

fun AnAction.performActionIfEnabled(dataContext: DataContext, place: String) {
  val anActionEvent = AnActionEvent.createEvent(
    this,
    dataContext,
    null,
    place,
    ActionUiKind.NONE,
    null,
  )
  ActionUtil.performDumbAwareUpdate(this, anActionEvent, false)
  if (anActionEvent.presentation.isEnabled) {
    ActionUtil.performActionDumbAwareWithCallbacks(this, anActionEvent)
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
