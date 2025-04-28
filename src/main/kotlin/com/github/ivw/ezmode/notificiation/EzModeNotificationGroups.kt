package com.github.ivw.ezmode.notificiation
// TODO fix package name

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.actions.*
import com.github.ivw.ezmode.keymap.*
import com.intellij.notification.*
import java.io.*

object ParserNotifications {
  val group: NotificationGroup by lazy {
    NotificationGroupManager.getInstance()
      .getNotificationGroup("ezmode.parser")
  }

  fun notifyError(file: File, e: Throwable) {
    val title = EzModeBundle.message("notificationGroup.ezmode.parser.failed.title", file.name)
    val content: String = if (e is EzModeRcParser.ParseError) {
      e.toNiceString()
    } else {
      e.message ?: e.javaClass.name
    }
    group.createNotification(
      title, content, NotificationType.WARNING
    ).also {
      it.addAction(OpenEzModeRcAction(file))
    }.notify(null)
  }
}
