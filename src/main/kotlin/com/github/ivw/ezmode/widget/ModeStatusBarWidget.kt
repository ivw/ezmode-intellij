package com.github.ivw.ezmode.widget

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.util.*
import com.intellij.openapi.wm.*
import kotlinx.coroutines.flow.*
import java.awt.*

// Implementation using `Flow` inspired by `com.intellij.openapi.wm.impl.status.PositionPanel`
@Suppress("UnstableApiUsage")
class ModeStatusBarWidget(
  private val dataContext: WidgetPresentationDataContext,
) : TextWidgetPresentation {
  override fun text(): Flow<@NlsContexts.Label String?> =
    combine(modeChangedFlow, dataContext.currentFileEditor) { _, fileEditor ->
      (fileEditor as? TextEditor)?.editor?.getMode()
    }

  override val alignment: Float
    get() = Component.CENTER_ALIGNMENT
}
