package com.github.ivw.ezmode.editor

import com.github.ivw.ezmode.cheatsheet.*
import com.github.ivw.ezmode.config.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.actionSystem.*

/**
 * Application-wide TypedActionHandler replacement.
 * TypedActionHandler does not affect non-typing actions such as control/alt keys.
 */
class EzModeTypedActionHandler(
  val originalHandler: TypedActionHandler,
) : TypedActionHandler {
  override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
    val config = service<EzModeConfigAppService>().getConfig()

    editor.project?.service<KeystrokeHistoryService>()?.add(charTyped.toString())

    EzModeKeyEvent(
      config = config,
      mode = editor.getMode(),
      char = charTyped,
      dataContext = dataContext,
      editor = editor,
      nativeHandler = originalHandler,
    ).perform(null)
  }

  companion object {
    val LOG = logger<EzModeTypedActionHandler>()
  }
}

fun TypedAction.ensureEzModeLoaded() {
  if (rawHandler !is EzModeTypedActionHandler) {
    setupRawHandler(EzModeTypedActionHandler(rawHandler))
    EzModeTypedActionHandler.LOG.info("EzModeTypedActionHandler loaded")
  } else {
    EzModeTypedActionHandler.LOG.info("EzModeTypedActionHandler already loaded")
  }
}

fun TypedAction.ensureEzModeUnloaded() {
  val rawHandler = rawHandler
  if (rawHandler is EzModeTypedActionHandler) {
    setupRawHandler(rawHandler.originalHandler)
    EzModeTypedActionHandler.LOG.info("EzModeTypedActionHandler unloaded")
  } else {
    EzModeTypedActionHandler.LOG.info("EzModeTypedActionHandler already unloaded")
  }
}
