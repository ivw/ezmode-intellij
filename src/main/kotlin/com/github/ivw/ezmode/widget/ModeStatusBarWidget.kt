package com.github.ivw.ezmode.widget

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.actions.*
import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.*
import com.intellij.openapi.project.*
import com.intellij.openapi.ui.popup.*
import com.intellij.openapi.wm.*
import com.intellij.ui.*
import com.intellij.ui.components.*
import org.jetbrains.annotations.*
import java.awt.event.*

class ModeStatusBarWidget(val project: Project) : CustomStatusBarWidget {
  private val modeService = project.service<ModeService>()

  private val label: JBLabel by lazy {
    JBLabel(modeService.getMode()).also { label ->
      label.toolTipText = EzModeBundle.message("ezmode.ModeStatusBarWidget.tooltip")

      (object : ClickListener() {
        override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
          (ActionManager.getInstance().getAction("ezmode.EzModeActionGroup") as? EzModeActionGroup)
            ?.createPopup(modeService.focusedEditor)
            ?.show(
              PopupShowOptions.aboveComponent(label)
                .withPopupComponentUnscaledGap(0)
            )
          return true
        }
      }).installOn(label, true)
    }
  }

  override fun getComponent() = label

  override fun ID(): @NonNls String = ModeStatusBarWidgetFactory.ID

  override fun install(statusBar: StatusBar) {
    project.subscribeToFocusOrModeChange(this) { mode, _ ->
      label.text = mode
    }
  }
}
