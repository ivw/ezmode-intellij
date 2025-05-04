package com.github.ivw.ezmode.config.keyactions

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.diagnostic.*

data class IdeKeyAction(val actionId: String) : KeyAction() {
  val anAction: AnAction? by lazy {
    ActionManager.getInstance().getAction(actionId)
      .also { if (it == null) LOG.info("Action not found: $actionId") }
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
      } else {
        LOG.info("Action not enabled: $actionId")
      }
    }
  }

  override fun toNiceString(): String =
    anAction?.templatePresentation?.description
      ?: anAction?.templateText
      ?: actionId

  companion object {
    val LOG = logger<IdeKeyAction>()
  }
}
