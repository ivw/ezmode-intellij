package com.github.ivw.ezmode.config

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.keymap.*

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
  val tabShortcut = KeyboardShortcut.fromString("TAB")
  val newTabShortcut = KeyboardShortcut.fromString("alt T")

  val ezModeUsesTab = keymap.getShortcuts("ezmode.EnterEzMode").contains(tabShortcut)
  if (ezModeUsesTab) {
    keymap.removeShortcut("EditorTab", tabShortcut)
    keymap.removeShortcut("EditorIndentSelection", tabShortcut)
    keymap.addShortcut("EditorTab", newTabShortcut)
    keymap.addShortcut("EditorIndentSelection", newTabShortcut)
  }
}
