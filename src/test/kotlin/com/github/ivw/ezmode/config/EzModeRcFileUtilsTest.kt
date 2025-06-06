package com.github.ivw.ezmode.config

import io.kotest.matchers.*
import org.junit.*

class EzModeRcFileUtilsTest {
  @Test
  fun testParseBaseRcFile() {
    val config = EzModeConfig()
    EzModeRcFileUtils.parseBaseRcFile(config)
    config.getBindingOrDefault("type", 'a').shouldBe(
      KeyBinding(null, KeyAction.Native)
    )
  }
}
