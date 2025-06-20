package com.github.ivw.ezmode.config

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.keymap.*

private val tabShortcut = KeyboardShortcut.fromString("TAB")
private val newTabShortcut = KeyboardShortcut.fromString("alt T")
private val tabActionsToMove: List<String> = listOf(
  "EditorTab",
  "EditorIndentSelection",
  "ExpandLiveTemplateByTab"
)

val Keymap.ezModeUsesTab
  get() = getShortcuts("ezmode.EnterEzMode").contains(tabShortcut)

/**
 * Since tab is used to enter EzMode by default,
 * we move the normal tab/indent actions to another shortcut,
 * so they don't conflict.
 *
 * This is skipped if the user changed the EzMode shortcut to something other than tab.
 *
 * This change does not persist; it is reset when the application restarts.
 */
fun moveTabShortcuts() {
  val keymap = KeymapManager.getInstance().activeKeymap
  if (keymap.ezModeUsesTab) {
    tabActionsToMove.forEach { actionId ->
      keymap.removeShortcut(actionId, tabShortcut)
      keymap.addShortcut(actionId, newTabShortcut)
    }
  }
}

fun restoreTabShortcuts() {
  val keymap = KeymapManager.getInstance().activeKeymap
  if (keymap.ezModeUsesTab) {
    tabActionsToMove.forEach { actionId ->
      keymap.removeShortcut(actionId, newTabShortcut)
      keymap.addShortcut(actionId, tabShortcut)
    }
  }
}
