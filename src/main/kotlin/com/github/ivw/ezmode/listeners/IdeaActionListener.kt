package com.github.ivw.ezmode.listeners

import com.github.ivw.ezmode.cheatsheet.*
import com.github.ivw.ezmode.config.keyactions.EZMODE_ACTION_PLACE
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.*
import com.intellij.openapi.components.*

class IdeaActionListener : AnActionListener {
  override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
    if (event.place == EZMODE_ACTION_PLACE) return

    event.project?.let { project ->
      ActionManager.getInstance().getId(action)?.let { id ->
        val translatedId = when (id) {
          "ezmode.EnterEzMode" -> "<mode ez>"
          "EditorEscape" -> "<Esc>"
          else -> "<idea $id>"
        }
        project.service<KeystrokeHistoryService>().add(translatedId)
      }
    }
  }
}
