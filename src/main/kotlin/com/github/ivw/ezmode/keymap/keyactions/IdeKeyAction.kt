package com.github.ivw.ezmode.keymap.keyactions

import com.github.ivw.ezmode.keymap.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.diagnostic.*

data class IdeKeyAction(val actionId: String) : KeyAction() {
  val anAction: AnAction? by lazy {
    ActionManager.getInstance().getAction(actionId)
      .also { if (it == null) thisLogger().info("Action $actionId not found") }
  }

  override fun perform(e: EzModeKeyEvent) {
    anAction?.let { anAction ->
      val presentation = Presentation()
      val anActionEvent = AnActionEvent.createFromDataContext(
        "ezmode", presentation, e.dataContext
      )
      anAction.update(anActionEvent)
      if (presentation.isEnabled) {
        anAction.actionPerformed(anActionEvent)
      }
    }
  }

  override fun toNiceString(): String =
    anAction?.templatePresentation?.description ?: anAction?.templateText ?: actionId
}
