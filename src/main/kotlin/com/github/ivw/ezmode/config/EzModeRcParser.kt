package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.*
import com.github.ivw.ezmode.config.keyactions.*
import com.github.ivw.ezmode.config.textobjects.*
import com.intellij.openapi.diagnostic.*
import java.awt.*
import java.util.*

object EzModeRcParser {
  const val COMMENT_PREFIX = '#'

  class ParseError(val lineIndex: Int, val c: Throwable) : RuntimeException(
    "line ${lineIndex + 1}: ${c.message ?: c.javaClass.name}", c
  ) {
    fun toNiceString(): String =
      EzModeBundle.message(
        "ezmode.ParseError.message",
        lineIndex + 1,
        c.message ?: c.javaClass.name
      )
  }

  class LineParseError(message: String) : RuntimeException(message)

  /**
   * @param dest The destination config.
   * @param lines The string input.
   * @param src A config from which keys can be mapped.
   * @throws ParseError
   */
  fun parse(dest: EzModeConfig, lines: List<String>, src: EzModeConfig?) =
    lines.forEachIndexed { lineIndex, line ->
      line.trim().takeIf { it.isNotEmpty() }?.let { line ->
        try {
          parseLine(dest, line, src)
        } catch (e: Throwable) {
          throw ParseError(lineIndex, e)
        }
      }
    }

  /**
   * @param dest The destination config.
   * @param line The string input.
   * @param src A key map from which keys can be mapped.
   * @throws LineParseError or another kind of exception.
   */
  fun parseLine(dest: EzModeConfig, line: String, src: EzModeConfig?) {
    if (line[0] == COMMENT_PREFIX) {
      return
    }

    val scanner = Scanner(line)
    val keyword = scanner.next()
    when (keyword) {
      "set" -> {
        val varName = scanner.next()
        val varValue = scanner.restOfLine()
        dest.vars[varName] = varValue
      }

      "map" -> {
        val mode = scanner.next()
        val char = parseChar(scanner)
        parseActionChain(scanner.restOfLine(), src)?.let { action ->
          dest.addBinding(mode, KeyBinding(char, action))
        }
      }

      else -> {
        throw LineParseError("unknown line keyword '$keyword'")
      }
    }
  }

  fun parseChar(scanner: Scanner): Char? {
    val charString = scanner.next()
    if (charString == "<default>") return null
    if (charString == "<space>") return ' '
    return charString.single()
  }

  fun parseActionChain(actionChainString: String, src: EzModeConfig?): KeyAction? {
    var charIndex = 0
    val actions = mutableListOf<KeyAction>()
    do {
      if (actionChainString[charIndex] == '<') {
        val closingIndex = actionChainString.indexOf('>', charIndex)
        if (closingIndex == -1) {
          throw LineParseError("missing closing bracket in: $actionChainString")
        }
        actions.add(
          parseSpecialAction(
            actionChainString.substring(charIndex + 1, closingIndex),
            src,
          )
        )
        charIndex = closingIndex + 1
      } else {
        actions.add(KeyAction.OfKeyChar(actionChainString[charIndex], src))
        charIndex++
      }
    } while (charIndex < actionChainString.length)

    return if (actions.size == 1) {
      actions[0]
    } else {
      KeyAction.Composite(actions)
    }
  }

  fun parseSpecialAction(specialActionString: String, src: EzModeConfig?): KeyAction {
    val scanner = Scanner(specialActionString)
    val keyword = scanner.next()
    return when (keyword) {
      "write" -> WriteAction(scanner.restOfLine())
      "writevar" -> WriteVarAction(scanner.restOfLine())
      "mode" -> KeyAction.ChangeMode(scanner.restOfLine())
      "idea" -> IdeKeyAction(scanner.restOfLine())
      "native" -> {
        if (scanner.hasNext()) {
          KeyAction.NativeOf(scanner.restOfLine().single())
        } else {
          KeyAction.Native
        }
      }

      "ofmode" -> KeyAction.OfMode(scanner.restOfLine(), src)
      "nop" -> KeyAction.Nop
      "pair" -> {
        val findClosingDelim: Boolean = when (scanner.next()) {
          "open" -> false
          "close" -> true
          else -> {
            throw LineParseError("first argument of `pair` must be open or close")
          }
        }
        val delims = mutableListOf<Delim>()
        do {
          val delim: Delim = scanner.next().let { pairChars ->
            when (pairChars) {
              "angle" -> PairDelim.angleBrackets
              "xml" -> XmlTagDelim
              else -> {
                if (pairChars.length != 2) {
                  throw LineParseError("invalid pair argument: $pairChars")
                }
                if (pairChars[0] == pairChars[1]) {
                  throw LineParseError("pair chars must be different: $pairChars")
                }
                PairDelim(pairChars[0], pairChars[1])
              }
            }
          }
          delims.add(delim)
        } while (scanner.hasNext())
        PairOpenCloseAction(findClosingDelim, delims)
      }

      "quote" -> QuoteAction(QuoteDelim(scanner.next().single()))
      "toolwindow" -> ToggleToolWindowAction(scanner.restOfLine())
      "numberop" -> NumberOperationAction(scanner.restOfLine())
      else -> {
        throw LineParseError("unknown action keyword: $keyword")
      }
    }
  }

  private fun Scanner.restOfLine() = nextLine().trimStart()

  fun parseColor(colorString: String): Color? =
    try {
      Color.decode(colorString)
    } catch (t: Throwable) {
      thisLogger().info(t)
      null
    }
}
