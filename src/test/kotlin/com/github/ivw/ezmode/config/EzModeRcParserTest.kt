package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.config.keyactions.*
import com.github.ivw.ezmode.editor.*
import io.kotest.assertions.throwables.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*
import io.kotest.matchers.types.*
import org.junit.*
import java.awt.*

class EzModeRcParserTest {
  @Test
  fun parse() {
    val config = EzModeConfig()
    """
      # This line is a comment.
      map ez A <idea EditorSelectLine>
      map ez t <mode type>
      map ez g <write git abc>
      map ez d <native>
      map ez D <native q>
    """.lines().let { lines ->
      EzModeRcParser.parse(config, lines, null)
    }
    config.modes.shouldBeSingleton { mode ->
      mode.name.shouldBe("ez")
      mode.keyBindings.values.shouldContainExactlyInAnyOrder(
        KeyBinding('A', IdeKeyAction("EditorSelectLine")),
        KeyBinding('t', KeyAction.ChangeMode("type")),
        KeyBinding('g', WriteAction("git abc")),
        KeyBinding('d', KeyAction.Native),
        KeyBinding('D', KeyAction.NativeOf('q')),
      )
    }

    val childConfig = EzModeConfig()
    """
      map ez m <idea ${"$"}SelectAll>gg
      map ez <space> g
    """.lines().let { lines ->
      EzModeRcParser.parse(childConfig, lines, config)
    }
    childConfig.modes.shouldBeSingleton { mode ->
      mode.name.shouldBe("ez")
      mode.keyBindings.values.shouldContainExactlyInAnyOrder(
        KeyBinding(
          'm', KeyAction.Composite(
            listOf(
              IdeKeyAction("${"$"}SelectAll"),
              KeyAction.OfKeyChar('g', config),
              KeyAction.OfKeyChar('g', config),
            )
          )
        ),
        KeyBinding(' ', KeyAction.OfKeyChar('g', config))
      )
    }
  }

  @Test
  fun parsePairAction() {
    val config = EzModeConfig()
    """
      map ez { <pair open {}>
      map ez > <pair close angle>
    """.lines().let { lines ->
      EzModeRcParser.parse(config, lines, null)
    }

    config.modes.shouldBeSingleton { mode ->
      mode.name.shouldBe("ez")
      mode.keyBindings.values.shouldContainExactlyInAnyOrder(
        KeyBinding(
          '{',
          PairOpenCloseAction(
            isTargetOpen = true,
            DelimPair.curlyBraces
          ),
        ),
        KeyBinding(
          '>',
          PairOpenCloseAction(
            isTargetOpen = false,
            DelimPair.angleBrackets
          ),
        )
      )
    }

    """
      map ez { <pair hello {}>
    """.lines().let { lines ->
      shouldThrow<EzModeRcParser.ParseError> {
        EzModeRcParser.parse(config, lines, null)
      }.cause.shouldNotBeNull().message.shouldBe(
        "first argument of `pair` must be open or close"
      )
    }

    """
      map ez [ <pair open [[]]>
    """.lines().let { lines ->
      shouldThrow<EzModeRcParser.ParseError> {
        EzModeRcParser.parse(config, lines, null)
      }.cause.shouldNotBeNull().message.shouldBe(
        "pair argument must have 2 chars: [[]]"
      )
    }

    """
      map ez " <pair open "">
    """.lines().let { lines ->
      shouldThrow<EzModeRcParser.ParseError> {
        EzModeRcParser.parse(config, lines, null)
      }.cause.shouldNotBeNull().message.shouldBe(
        "pair chars must be different: \"\""
      )
    }
  }

  @Test
  fun parseVar() {
    val config = EzModeConfig()
    """
      set defaultmode ez
      set primarycolor #abcdef
      set myvar This var has multiple words
    """.lines().let { lines ->
      EzModeRcParser.parse(config, lines, null)
    }
    config.defaultMode.shouldBe(Mode.EZ)
    config.primaryColor.shouldBeInstanceOf<Color>().red.shouldBe(0xAB)
    config.vars["myvar"].shouldBe("This var has multiple words")
  }

  @Test
  fun parseInvalidPrimaryColor() {
    val config = EzModeConfig()
    """
      set primarycolor wrong
    """.lines().let { lines ->
      EzModeRcParser.parse(config, lines, null)
    }
    config.primaryColor.shouldBe(null)
  }

  @Test
  fun parseActionChain() {
    EzModeRcParser.parseActionChain("<mode select>kk<mode ez>", null)
      .shouldNotBeNull()
      .toNiceString().shouldBe("Switch mode to select, kk, Switch mode to ez")
  }
}
