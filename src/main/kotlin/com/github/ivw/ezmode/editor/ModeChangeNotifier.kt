package com.github.ivw.ezmode.editor

import com.intellij.openapi.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.project.*
import com.intellij.util.messages.*

/**
 * Notifies whenever the focused editor or its mode changes.
 */
interface FocusOrModeChangeNotifier {
  fun onChanged(mode: String, editor: Editor)

  companion object {
    @Topic.ProjectLevel
    val TOPIC = Topic(FocusOrModeChangeNotifier::class.java)
  }
}

fun Project.subscribeToFocusOrModeChange(
  parentDisposable: Disposable,
  onChanged: (mode: String, editor: Editor) -> Unit,
) {
  messageBus.connect(parentDisposable).subscribe(
    FocusOrModeChangeNotifier.TOPIC,
    object : FocusOrModeChangeNotifier {
      override fun onChanged(mode: String, editor: Editor) {
        onChanged(mode, editor)
      }
    }
  )
}
