package com.github.ivw.ezmode.keymap

import com.github.ivw.ezmode.keymap.keyactions.*
import java.util.*

object EzModeRcParser {
  class ParseError(val lineIndex: Int, cause: Throwable) : RuntimeException(
    "line ${lineIndex + 1}", cause
  )

  class LineParseError(message: String) : RuntimeException(message)

  /**
   * @param dest The destination key map for all key binding changes.
   * @param lines The string input.
   * @param src A key map from which keys can be mapped.
   * @return a `MutableEzModeKeyMap` populated with the given input.
   * @throws ParseError
   */
  fun parse(dest: MutableEzModeKeyMap, lines: List<String>, src: EzModeKeyMap?) =
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
   * @param dest The destination key map for all key binding changes.
   * @param line The string input.
   * @param src A key map from which keys can be mapped.
   * @throws LineParseError or another kind of exception.
   */
  fun parseLine(dest: MutableEzModeKeyMap, line: String, src: EzModeKeyMap?) {
    if (line[0] == '#') {
      // Line is a comment.
      return
    }

    val scanner = Scanner(line)
    val keyword = scanner.next()
    when (keyword) {
      "map" -> {
        val mode = scanner.next()
        val char = parseChar(scanner)
        parseActionChain(scanner.restOfLine(), src, mode)?.let { action ->
          dest.addBinding(KeyBinding(mode, char, action))
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

  fun parseActionChain(
    actionChainString: String,
    src: EzModeKeyMap?,
    mode: String,
  ): KeyAction? {
    var charIndex = 0
    val actions = mutableListOf<KeyAction>()
    do {
      if (actionChainString[charIndex] == '<') {
        val closingIndex = actionChainString.indexOf('>', charIndex)
        if (closingIndex == -1) {
          throw LineParseError("missing closing bracket in: $actionChainString")
        }
        actions.add(parseSpecialAction(actionChainString.substring(charIndex + 1, closingIndex)))
        charIndex = closingIndex + 1
      } else {
        src?.getBindingOrDefault(mode, actionChainString[charIndex])?.action?.let {
          actions.add(it)
        }
        charIndex++
      }
    } while (charIndex < actionChainString.length)

    return if (actions.size == 1) {
      actions[0]
    } else {
      KeyAction.Composite(actions)
    }
  }

  fun parseSpecialAction(specialActionString: String): KeyAction {
    val scanner = Scanner(specialActionString)
    val keyword = scanner.next()
    return when (keyword) {
      "type" -> InsertStringAction(scanner.restOfLine())
      "mode" -> KeyAction.ChangeMode(scanner.restOfLine())
      "idea" -> IdeKeyAction(scanner.restOfLine())
      "native" -> {
        if (scanner.hasNext()) {
          KeyAction.NativeOf(scanner.restOfLine().single())
        } else {
          KeyAction.Native
        }
      }

      "ofmode" -> KeyAction.OfMode(scanner.restOfLine())
      "nop" -> KeyAction.Nop
      "pair" -> {
        val direction = scanner.next()
        val isTargetOpen: Boolean = when (direction) {
          "open" -> true
          "close" -> false
          else -> {
            throw LineParseError("first argument of `pair` must be open or close")
          }
        }

        val pairChars = scanner.next()
        if (pairChars.length != 2) {
          throw LineParseError("pair argument must have 2 chars: $pairChars")
        }
        if (pairChars[0] == pairChars[1]) {
          throw LineParseError("pair chars must be different: $pairChars")
        }
        PairOpenCloseAction(isTargetOpen, DelimPair(pairChars[0], pairChars[1]))
      }

      else -> {
        throw LineParseError("unknown action keyword: $keyword")
      }
    }
  }

  private fun Scanner.restOfLine() = nextLine().trimStart()
}
