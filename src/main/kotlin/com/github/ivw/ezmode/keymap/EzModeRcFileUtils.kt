package com.github.ivw.ezmode.keymap

import com.github.ivw.ezmode.notification.*
import com.intellij.openapi.diagnostic.*
import java.io.*

object EzModeRcFileUtils {
  const val BASE_RC_RESOURCE_NAME = "base.ezmoderc"
  const val USER_RC_NAME = ".ezmoderc"
  const val USER_IDEA_RC_NAME = ".idea.ezmoderc"

  val RC_FILE_TEMPLATE: String = """
    # .ezmoderc lets you define custom key mappings for ezmode.
    
    # map {mode} {key} {actions}
    map ez 6 <write Hello world><mode type>
  """.trimIndent()

  private val homePathName: String by lazy {
    System.getProperty("user.home")
  }

  class BaseRcResourceMissingError(message: String) : RuntimeException(message)

  fun parseBaseRcFile(dest: MutableEzModeKeyMap) {
    (javaClass.getResourceAsStream(BASE_RC_RESOURCE_NAME)
      ?: throw BaseRcResourceMissingError(BASE_RC_RESOURCE_NAME))
      .let { inputStream ->
        LOG.info("Parsing $BASE_RC_RESOURCE_NAME")
        EzModeRcParser.parse(dest, inputStream.bufferedReader().readLines(), null)
      }
  }

  fun getUserRcFile(fileName: String): File =
    File(homePathName, fileName)

  fun ensureUserRcFileExists(fileName: String): File {
    val file = getUserRcFile(fileName)
    if (!file.exists()) {
      file.createNewFile()
      file.writeText(RC_FILE_TEMPLATE)
    }
    return file
  }

  fun parseUserRcFile(fileName: String, dest: MutableEzModeKeyMap) {
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

  fun getKeyMap(): MutableEzModeKeyMap {
    val keyMap = MutableEzModeKeyMap()
    parseBaseRcFile(keyMap)
    parseUserRcFile(USER_RC_NAME, keyMap)
    parseUserRcFile(USER_IDEA_RC_NAME, keyMap)
    return keyMap
  }

  private val LOG = logger<EzModeRcFileUtils>()
}
