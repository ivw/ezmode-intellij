package com.github.ivw.ezmode.notificiation

import com.intellij.notification.*

object EzModeNotificationGroups {
  val parser: NotificationGroup? by lazy {
    NotificationGroupManager.getInstance()
      .getNotificationGroup("ezmode.parser")
  }
}
