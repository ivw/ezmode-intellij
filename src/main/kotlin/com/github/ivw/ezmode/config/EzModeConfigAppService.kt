package com.github.ivw.ezmode.config

import com.intellij.openapi.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.util.messages.*

@Service
class EzModeConfigAppService {
  private var config: EzModeConfig? = null

  val application: Application = ApplicationManager.getApplication()

  fun loadConfig(): EzModeConfig =
    EzModeRcFileUtils.getConfig().also {
      config = it
      application.messageBus.syncPublisher(CONFIG_CHANGE_TOPIC)
        .onChanged(it)
    }

  fun getConfig(): EzModeConfig = config ?: loadConfig()

  fun subscribeToConfig(parentDisposable: Disposable, onChanged: (EzModeConfig) -> Unit) {
    application.messageBus.connect(parentDisposable).subscribe(
      CONFIG_CHANGE_TOPIC,
      object : ConfigChangeNotifier {
        override fun onChanged(config: EzModeConfig) {
          onChanged(config)
        }
      }
    )
  }

  interface ConfigChangeNotifier {
    fun onChanged(config: EzModeConfig)
  }

  companion object {
    @Topic.AppLevel
    val CONFIG_CHANGE_TOPIC = Topic(ConfigChangeNotifier::class.java)
  }
}
