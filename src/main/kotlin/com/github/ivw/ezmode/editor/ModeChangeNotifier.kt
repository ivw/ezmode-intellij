package com.github.ivw.ezmode.editor

import com.intellij.openapi.*
import com.intellij.openapi.application.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.ex.*
import com.intellij.util.messages.*
import java.awt.event.*

interface ModeChangeNotifier {
  fun onChanged(mode: String, editor: Editor)

  companion object {
    @Topic.AppLevel
    val TOPIC = Topic(ModeChangeNotifier::class.java)
  }
}

fun Application.subscribeToModeChange(
  parentDisposable: Disposable,
  onChanged: (mode: String, editor: Editor) -> Unit,
) {
  messageBus.connect(parentDisposable).subscribe(
    ModeChangeNotifier.TOPIC,
    object : ModeChangeNotifier {
      override fun onChanged(mode: String, editor: Editor) {
        onChanged(mode, editor)
      }
    }
  )
}

fun Application.subscribeToFocusedEditorModeChange(
  parentDisposable: Disposable,
  onChanged: (mode: String, editor: Editor) -> Unit,
) {
  var lastFocusedEditor: Editor? = null

  subscribeToModeChange(parentDisposable) { mode, editor ->
    if (lastFocusedEditor == null || editor === lastFocusedEditor) {
      onChanged(mode, editor)
    }
  }

  (EditorFactory.getInstance().eventMulticaster as? EditorEventMulticasterEx)?.addFocusChangeListener(
    object : FocusChangeListener {
      override fun focusGained(editor: Editor, event: FocusEvent) {
        lastFocusedEditor = editor
        onChanged(editor.getMode(), editor)
      }
    },
    parentDisposable,
  )
}
