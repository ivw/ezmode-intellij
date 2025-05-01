package com.github.ivw.ezmode.config

import com.github.ivw.ezmode.config.keyactions.*
import com.github.ivw.ezmode.editor.*
import io.kotest.assertions.throwables.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*
import org.junit.*

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
    val gAction = WriteAction("git abc")
    config.keyMap.values.shouldContainExactly(
      KeyBinding("ez", 'A', IdeKeyAction("EditorSelectLine")),
      KeyBinding("ez", 't', KeyAction.ChangeMode("type")),
      KeyBinding("ez", 'g', gAction),
      KeyBinding("ez", 'd', KeyAction.Native),
      KeyBinding("ez", 'D', KeyAction.NativeOf('q')),
    )

    val childConfig = EzModeConfig()
    """
      map ez m <idea ${"$"}SelectAll>gg
      map ez <space> g
    """.lines().let { lines ->
      EzModeRcParser.parse(childConfig, lines, config)
    }
    childConfig.keyMap.values.shouldContainExactly(
      KeyBinding(
        "ez", 'm', KeyAction.Composite(
          listOf(
            IdeKeyAction("${"$"}SelectAll"),
            gAction,
            gAction,
          )
        )
      ),
      KeyBinding("ez", ' ', gAction)
    )
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

    config.keyMap.values.shouldContainExactly(
      KeyBinding(
        "ez", '{',
        PairOpenCloseAction(
          isTargetOpen = true,
          DelimPair('{', '}')
        ),
      ),
      KeyBinding(
        "ez", '>',
        PairOpenCloseAction(
          isTargetOpen = false,
          DelimPair('<', '>')
        ),
      )
    )

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
      set myvar This var has multiple words
    """.lines().let { lines ->
      EzModeRcParser.parse(config, lines, null)
    }
    config.defaultMode.shouldBe(Mode.EZ)
    config.vars["myvar"].shouldBe("This var has multiple words")
  }
}
