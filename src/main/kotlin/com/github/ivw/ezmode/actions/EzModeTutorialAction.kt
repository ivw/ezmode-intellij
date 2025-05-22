package com.github.ivw.ezmode.actions

import com.intellij.ide.scratch.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.*

class EzModeTutorialAction : DumbAwareAction() {
  override fun actionPerformed(e: AnActionEvent) {
    e.project?.let(::openTutorial)
  }

  companion object {
    val tutorialText: String? by lazy {
      EzModeTutorialAction::class.java.getResourceAsStream("tutorial.md")
        ?.bufferedReader()?.use { it.readText() }
    }

    fun openTutorial(project: Project) {
      val tutorialText = tutorialText ?: return

      val scratchFile = ScratchRootType.getInstance().createScratchFile(
        project, "EzModeTutorial.md", null, tutorialText,
      ) ?: return

      FileEditorManager.getInstance(project).openFile(scratchFile, true)
        .forEach { editor ->
          // Close the markdown preview:
          TextEditorWithPreview.getParentSplitEditor(editor)?.setLayout(
            TextEditorWithPreview.Layout.SHOW_EDITOR
          )
        }
    }
  }
}
