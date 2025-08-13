package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.editor.*
import com.intellij.openapi.editor.*

fun resolveVar(varName: String, e: EzModeEvent, caretIndex: Int?, caret: Caret?): String? =
  when (varName) {
    "caretindex" -> caretIndex?.toString()
    "line" -> caret?.let { (it.logicalPosition.line + 1).toString() }
    "column" -> caret?.let { (it.logicalPosition.column + 1).toString() }
    "filename" -> e.editor?.virtualFile?.nameWithoutExtension
    "projectname" -> e.project?.name
    "mode" -> e.editor?.getMode()
    "key" -> if (e is EzModeKeyEvent) e.char.toString() else null
    "space" -> " "
    "tab" -> "\t"
    "nl" -> "\n"
    else -> e.config.vars[varName]
  }

/**
 * Matches a word inside ${}
 */
val varRegex by lazy { """\$\{([A-Za-z_]\w*)\}""".toRegex() }

fun String.resolveVars(e: EzModeEvent, caretIndex: Int?, caret: Caret?): String =
  replace(varRegex) { matchResult ->
    val varName = matchResult.groupValues[1]
    resolveVar(varName, e, caretIndex, caret) ?: matchResult.value
  }

fun CaretModel.runForEachCaretIndexed(action: (caret: Caret, caretIndex: Int) -> Unit) {
  var caretIndex = 0
  runForEachCaret {
    @Suppress("AssignedValueIsNeverRead")
    action(it, caretIndex++)
  }
}
