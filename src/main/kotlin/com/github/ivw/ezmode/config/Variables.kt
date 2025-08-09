package com.github.ivw.ezmode.config

import com.intellij.openapi.editor.*

fun resolveVar(varName: String, e: EzModeKeyEvent, caretIndex: Int, caret: Caret): String? =
  when (varName) {
    "caretindex" -> caretIndex.toString()
    "line" -> (caret.logicalPosition.line + 1).toString()
    "column" -> (caret.logicalPosition.column + 1).toString()
    "filename" -> e.editor.virtualFile.nameWithoutExtension
    "projectname" -> e.project?.name
    "mode" -> e.mode
    "key" -> e.char.toString()
    "space" -> " "
    "tab" -> "\t"
    "nl" -> "\n"
    "dollar" -> "$"
    else -> e.config.vars[varName]
  }

/**
 * Matches any word preceded by a $
 */
val varRegex by lazy { """\$\w+""".toRegex() }

fun String.resolveVars(e: EzModeKeyEvent, caretIndex: Int, caret: Caret): String =
  replace(varRegex) { matchResult ->
    // Take the substring after the `$` character:
    val varName = matchResult.value.substring(1)
    resolveVar(varName, e, caretIndex, caret) ?: matchResult.value
  }

fun CaretModel.runForEachCaretIndexed(action: (caret: Caret, caretIndex: Int) -> Unit) {
  var caretIndex = 0
  runForEachCaret {
    @Suppress("AssignedValueIsNeverRead")
    action(it, caretIndex++)
  }
}
