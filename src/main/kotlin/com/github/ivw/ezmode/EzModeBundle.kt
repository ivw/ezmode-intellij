package com.github.ivw.ezmode

import com.intellij.*
import org.jetbrains.annotations.*

@NonNls
private const val BUNDLE = "messages.EzModeBundle"

object EzModeBundle : DynamicBundle(BUNDLE) {
  @JvmStatic
  fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
    getMessage(key, *params)

  @JvmStatic
  fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
    getLazyMessage(key, *params)
}
