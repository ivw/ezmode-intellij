package com.github.ivw.ezmode.keymap

object EzModeRcFileUtils {
  class BaseRcResourceMissingError(message: String) : RuntimeException(message)

  const val BASE_RC_RESOURCE_NAME = "base.ezmoderc"

  fun readResourceRcFile(): List<String> =
    (javaClass.getResourceAsStream(BASE_RC_RESOURCE_NAME)
      ?: throw BaseRcResourceMissingError(BASE_RC_RESOURCE_NAME))
      .bufferedReader().readLines()
}
