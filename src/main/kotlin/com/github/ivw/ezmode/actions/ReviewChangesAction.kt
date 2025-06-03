package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.config.keyactions.*
import com.intellij.ide.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*

class ReviewChangesAction : DumbAwareAction() {
  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let { project ->
      ToolWindowManager.getInstance(project).getToolWindow("Commit")?.let { toolWindow ->
        toolWindow.show()
        toolWindow.contentManager.contents.getOrNull(0)?.preferredFocusableComponent?.let { component ->
          val componentDataContext = DataManager.getInstance().getDataContext(component)
          componentDataContext.getData(PlatformDataKeys.TREE_EXPANDER)?.expandAll()
          ActionManager.getInstance().getAction("Diff.ShowDiff")
            ?.performActionIfEnabled(componentDataContext, e.place)
        }
      }
    }
  }
}
