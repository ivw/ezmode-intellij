package com.github.ivw.ezmode.cheatsheet

import com.intellij.openapi.*
import com.intellij.openapi.project.*
import com.intellij.util.messages.*

interface KeystrokeHistoryNotifier {
  fun onChanged(history: String)

  companion object {
    @Topic.ProjectLevel
    val TOPIC = Topic(KeystrokeHistoryNotifier::class.java)
  }
}

fun Project.subscribeToKeystrokeHistory(
  parentDisposable: Disposable,
  onChanged: (history: String) -> Unit,
) {
  messageBus.connect(parentDisposable).subscribe(
    KeystrokeHistoryNotifier.TOPIC,
    object : KeystrokeHistoryNotifier {
      override fun onChanged(history: String) {
        onChanged(history)
      }
    }
  )
}
