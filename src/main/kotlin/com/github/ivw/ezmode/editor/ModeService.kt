package com.github.ivw.ezmode.editor

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.event.*
import com.intellij.openapi.editor.ex.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.*
import java.awt.event.*

@Service(Service.Level.PROJECT)
class ModeService(val project: Project) : Disposable {
  /**
   * Mode is only set at a project level.
   * The mode of an editor is derived from projectMode and the editor's selectModeLeadOffset.
   */
  var projectMode: String = Mode.TYPE
    private set

  private var focusedEditor: Editor? = null

  fun init() {
    service<EzModeConfigAppService>().getConfig().defaultMode?.let { projectMode = it }
    FileEditorManager.getInstance(project).selectedTextEditor?.let(::focusEditor)
    EditorFactory.getInstance().apply {
      (eventMulticaster as? EditorEventMulticasterEx)?.addFocusChangeListener(
        MyFocusChangeListener(), this@ModeService,
      )
      eventMulticaster.addSelectionListener(MySelectionListener(), this@ModeService)
    }
  }

  override fun dispose() {
  }

  private fun focusEditor(editor: Editor) {
    focusedEditor = editor
    handleFocusOrModeChange(editor)
  }

  private fun handleFocusOrModeChange(editor: Editor) {
    val editorMode = getMode(editor)
    editor.updateEditorColors(editorMode)
    project.messageBus.syncPublisher(FocusOrModeChangeNotifier.TOPIC)
      .onChanged(editorMode, editor)
    focusOrModeChangedFlow.tryEmit(Unit)
  }

  fun setMode(mode: String) {
    val projectModeOld = projectMode
    projectMode = if (mode == Mode.SELECT) Mode.EZ else mode
    focusedEditor?.let { editor ->
      if (mode == Mode.SELECT) {
        editor.setSelectModeLeadOffset()
      } else if (!editor.selectionModel.hasSelection()) {
        editor.setSelectModeLeadOffset(null)
      } else if (mode == Mode.EZ && projectModeOld == Mode.EZ) {
        editor.setSelectModeLeadOffset(null)
        editor.selectionModel.removeSelection()
      }
      handleFocusOrModeChange(editor)
    }
  }

  fun getMode(editor: Editor): String =
    if (projectMode == Mode.EZ && editor.getSelectModeLeadOffset() != null) {
      Mode.SELECT
    } else projectMode

  fun getMode(): String =
    focusedEditor?.let { getMode(it) } ?: projectMode

  private inner class MyFocusChangeListener : FocusChangeListener {
    override fun focusGained(editor: Editor, event: FocusEvent) {
      if (editor.project == project) {
        focusEditor(editor)
      }
    }

    override fun focusLost(editor: Editor, event: FocusEvent) {
      if (editor.project == project) {
        focusedEditor = null
      }
    }
  }

  private inner class MySelectionListener : SelectionListener {
    override fun selectionChanged(e: SelectionEvent) {
      if (e.editor.project == project) {
        if (e.newRange.isEmpty) {
          if (e.newRange.startOffset != e.editor.getSelectModeLeadOffset()) {
            e.editor.setSelectModeLeadOffset(null)
            handleFocusOrModeChange(e.editor)
          }
        } else if (e.oldRange.isEmpty) {
          e.editor.setSelectModeLeadOffset()
          handleFocusOrModeChange(e.editor)
        }
      }
    }
  }
}
