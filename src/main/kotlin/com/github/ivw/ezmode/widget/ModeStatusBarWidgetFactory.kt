package com.github.ivw.ezmode.widget

import com.github.ivw.ezmode.EzModeBundle
import com.intellij.openapi.util.*
import com.intellij.openapi.wm.*
import kotlinx.coroutines.*
import org.jetbrains.annotations.*

@Suppress("UnstableApiUsage")
class ModeStatusBarWidgetFactory : StatusBarWidgetFactory, WidgetPresentationFactory {
  override fun getId(): @NonNls String = ID

  override fun getDisplayName(): @NlsContexts.ConfigurableName String =
    EzModeBundle.message("ezmode.ModeStatusBarWidget.name")

  override fun createPresentation(context: WidgetPresentationDataContext, scope: CoroutineScope) =
    ModeStatusBarWidget(context)

  companion object {
    const val ID = "ezmode.ModeStatusBarWidget"
  }
}
