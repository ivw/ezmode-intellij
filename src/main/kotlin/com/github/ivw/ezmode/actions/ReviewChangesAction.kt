package com.github.ivw.ezmode.actions

import com.github.ivw.ezmode.config.keyactions.*
import com.github.ivw.ezmode.editor.*
import com.intellij.ide.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.fileEditor.ex.*
import com.intellij.openapi.project.*
import com.intellij.openapi.wm.*

class ReviewChangesAction : DumbAwareAction() {
  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let { project ->
      project.service<ModeService>().focusedEditor?.let { editor ->
        if (editor.editorKind == EditorKind.DIFF) {
          exitToSource(project, editor)
          return
        }
      }

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

  fun exitToSource(project: Project, editor: Editor) {
    val editorManager = FileEditorManagerEx.getInstanceEx(project)
    editorManager.currentFile?.let { editorManager.closeFile(it) }
    editorManager.openFile(editor.virtualFile, true)
    project.service<ModeService>().apply {
      if (projectMode == Mode.GIT) {
        setMode(Mode.EZ)
      }
    }
  }
}
