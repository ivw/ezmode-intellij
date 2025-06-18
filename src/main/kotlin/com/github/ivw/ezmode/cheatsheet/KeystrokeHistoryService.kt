package com.github.ivw.ezmode.cheatsheet

import com.intellij.openapi.components.*
import com.intellij.openapi.project.*

@Service(Service.Level.PROJECT)
class KeystrokeHistoryService(val project: Project) {
  private var buffer: String = ""

  fun add(s: String) {
    if (buffer.length > 2000) {
      buffer = buffer.substring(1000)
    }
    buffer += s
    notifyChange()
  }

  fun clear() {
    buffer = ""
    notifyChange()
  }

  private fun notifyChange() {
    project.messageBus.syncPublisher(KeystrokeHistoryNotifier.TOPIC)
      .onChanged(buffer)
  }

  fun getHistory(): String = buffer
}
