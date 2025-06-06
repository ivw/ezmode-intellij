package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.notification.*
import com.intellij.openapi.diagnostic.*
import java.io.*

object EzModeRcFileUtils {
  const val BASE_RC_RESOURCE_NAME = "base.ezmoderc"
  const val TEMPLATE_RC_RESOURCE_NAME = "template.ezmoderc"
  const val USER_RC_NAME = ".ezmoderc"
  const val USER_IDEA_RC_NAME = ".idea.ezmoderc"

  private val homePathName: String by lazy {
    System.getProperty("user.home")
  }

  class BaseRcResourceMissingError(message: String) : RuntimeException(message)

  fun parseBaseRcFile(dest: EzModeConfig) {
    (javaClass.getResourceAsStream(BASE_RC_RESOURCE_NAME)
      ?: throw BaseRcResourceMissingError(BASE_RC_RESOURCE_NAME))
      .let { inputStream ->
        LOG.info("Parsing $BASE_RC_RESOURCE_NAME")
        EzModeRcParser.parse(dest, inputStream.bufferedReader().readLines(), null)
      }
  }

  fun getRcFileTemplate(): String? =
    javaClass.getResourceAsStream(TEMPLATE_RC_RESOURCE_NAME)
      ?.bufferedReader()?.use { it.readText() }

  fun getUserRcFile(fileName: String): File =
    File(homePathName, fileName)

  fun ensureUserRcFileExists(fileName: String): File {
    val file = getUserRcFile(fileName)
    if (!file.exists()) {
      file.createNewFile()
      getRcFileTemplate()?.let(file::writeText)
    }
    return file
  }

  fun parseUserRcFile(fileName: String, dest: EzModeConfig) {
    getUserRcFile(fileName).takeIf { it.exists() }?.let { file ->
      LOG.info("Parsing $file")
      try {
        EzModeRcParser.parse(dest, file.bufferedReader().readLines(), dest.copy())
      } catch (e: Throwable) {
        LOG.info(e)
        ParserNotifications.notifyError(file, e)
      }
    }
  }

  fun getConfig(): EzModeConfig {
    val config = EzModeConfig()
    parseBaseRcFile(config)
    parseUserRcFile(USER_RC_NAME, config)
    parseUserRcFile(USER_IDEA_RC_NAME, config)
    return config
  }

  private val LOG = logger<EzModeRcFileUtils>()
}
