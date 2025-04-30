package com.github.ivw.ezmode.widget

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.util.*
import com.intellij.openapi.wm.*
import kotlinx.coroutines.flow.*
import java.awt.*
import java.awt.event.*

// Implementation using `Flow` inspired by `com.intellij.openapi.wm.impl.status.PositionPanel`
@Suppress("UnstableApiUsage")
class ModeStatusBarWidget(
  private val dataContext: WidgetPresentationDataContext,
) : TextWidgetPresentation {
  private fun FileEditor.getEditor() = (this as? TextEditor)?.editor

  private fun getEditor() = dataContext.currentFileEditor.value?.getEditor()

  override fun text(): Flow<@NlsContexts.Label String?> =
    combine(modeChangedFlow, dataContext.currentFileEditor) { _, fileEditor ->
      fileEditor?.getEditor()?.getMode()
    }

  override val alignment: Float
    get() = Component.CENTER_ALIGNMENT

  override fun getClickConsumer(): ((MouseEvent) -> Unit)? {
    return { mouseEvent ->
      val dataContext: DataContext = getEditor()?.let { editor ->
        SimpleDataContext.builder()
          .add(CommonDataKeys.PROJECT, editor.project)
          .add(CommonDataKeys.EDITOR, editor)
          .build()
      } ?: DataContext.EMPTY_CONTEXT
      ActionManager.getInstance().getAction("ezmode.EzModeActionGroup").actionPerformed(
        AnActionEvent.createFromInputEvent(
          mouseEvent, "ezmode.ModeStatusBarWidget", null,
          dataContext
        )
      )
    }
  }
}
