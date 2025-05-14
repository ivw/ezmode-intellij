package com.github.ivw.ezmode.widget

import com.github.ivw.ezmode.*
import com.intellij.openapi.project.*
import com.intellij.openapi.util.*
import com.intellij.openapi.wm.*
import org.jetbrains.annotations.*

class ModeStatusBarWidgetFactory : StatusBarWidgetFactory {
  override fun getId(): @NonNls String = ID

  override fun getDisplayName(): @NlsContexts.ConfigurableName String =
    EzModeBundle.message("ezmode.ModeStatusBarWidget.name")

  override fun createWidget(project: Project): StatusBarWidget =
    ModeStatusBarWidget(project)

  companion object {
    const val ID = "ezmode.ModeStatusBarWidget"
  }
}
