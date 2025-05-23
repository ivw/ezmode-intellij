package com.github.ivw.ezmode.editor

import com.github.ivw.ezmode.config.*
import com.intellij.openapi.*
import com.intellij.openapi.components.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.event.*
import com.intellij.openapi.editor.ex.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.*
import java.awt.*
import java.awt.event.*

@Service(Service.Level.PROJECT)
class ModeService(val project: Project) : Disposable {
  /**
   * Mode is only set at a project level.
   * The mode of an editor is derived from projectMode and the editor's selectModeLeadOffset.
   */
  var projectMode: String = Mode.TYPE
    private set

  var focusedEditor: Editor? = null
    private set

  var ezModeCaretColor: Color? = null
    private set

  fun init() {
    val configService = service<EzModeConfigAppService>()
    configService.getConfig().let { config ->
      config.defaultMode?.let { projectMode = it }
      config.caretColor?.let { ezModeCaretColor = it }
    }
    configService.subscribeToConfig(this) { config ->
      if (config.caretColor != ezModeCaretColor) {
        ezModeCaretColor = config.caretColor
        focusedEditor?.let { editor -> editor.updateEditorColors(getMode(editor), ezModeCaretColor) }
      }
    }

    FileEditorManager.getInstance(project).selectedTextEditor?.let(::focusEditorIfValid)
    EditorFactory.getInstance().apply {
      (eventMulticaster as? EditorEventMulticasterEx)?.addFocusChangeListener(
        MyFocusChangeListener(), this@ModeService,
      )
      eventMulticaster.addSelectionListener(MySelectionListener(), this@ModeService)
    }
  }

  override fun dispose() {
  }

  private fun isValidEditor(editor: Editor) =
    editor.project == project && (
      editor.editorKind == EditorKind.MAIN_EDITOR ||
        editor.editorKind == EditorKind.DIFF
      )

  private fun focusEditorIfValid(editor: Editor) {
    if (isValidEditor(editor)) {
      focusedEditor = editor
      handleFocusOrModeChange(editor)
    }
  }

  private fun handleFocusOrModeChange(editor: Editor) {
    val editorMode = getMode(editor)
    editor.updateEditorColors(editorMode, ezModeCaretColor)
    project.messageBus.syncPublisher(FocusOrModeChangeNotifier.TOPIC)
      .onChanged(editorMode, editor)
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
        editor.selectionModel.removeSelection(true)
      }
      handleFocusOrModeChange(editor)
    }
  }

  fun getMode(editor: Editor): String =
    if (isValidEditor(editor)) {
      if (projectMode == Mode.EZ && editor.getSelectModeLeadOffset() != null) {
        Mode.SELECT
      } else projectMode
    } else Mode.TYPE

  fun getMode(): String =
    focusedEditor?.let { getMode(it) } ?: projectMode

  private inner class MyFocusChangeListener : FocusChangeListener {
    override fun focusGained(editor: Editor, event: FocusEvent) {
      focusEditorIfValid(editor)
    }

    override fun focusLost(editor: Editor, event: FocusEvent) {
      if (isValidEditor(editor)) {
        focusedEditor = null
      }
    }
  }

  private inner class MySelectionListener : SelectionListener {
    override fun selectionChanged(e: SelectionEvent) {
      if (isValidEditor(e.editor)) {
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
