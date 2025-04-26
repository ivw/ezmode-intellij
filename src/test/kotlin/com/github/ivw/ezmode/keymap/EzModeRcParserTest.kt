package com.github.ivw.ezmode.keymap

import com.github.ivw.ezmode.keymap.keyactions.*
import io.kotest.assertions.throwables.*
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*
import org.junit.*

class EzModeRcParserTest {
  @Test
  fun parse() {
    val keyMap = MutableEzModeKeyMap()
    """
      # This line is a comment.
      map ez A <idea EditorSelectLine>
      map ez t <mode type>
      map ez g <type git abc>
      map ez d <native>
      map ez D <native q>
    """.lines().let { lines ->
      EzModeRcParser.parse(keyMap, lines, null)
    }
    val gAction = InsertStringAction("git abc")
    keyMap.values.shouldContainExactly(
      KeyBinding("ez", 'A', IdeKeyAction("EditorSelectLine")),
      KeyBinding("ez", 't', KeyAction.ChangeMode("type")),
      KeyBinding("ez", 'g', gAction),
      KeyBinding("ez", 'd', KeyAction.Native),
      KeyBinding("ez", 'D', KeyAction.NativeOf('q')),
    )

    val childKeyMap = MutableEzModeKeyMap()
    """
      map ez m <idea ${"$"}SelectAll>gg
      map ez <space> g
    """.lines().let { lines ->
      EzModeRcParser.parse(childKeyMap, lines, keyMap)
    }
    childKeyMap.values.shouldContainExactly(
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
    val keyMap = MutableEzModeKeyMap()
    """
      map ez { <pair open {}>
    """.lines().let { lines ->
      EzModeRcParser.parse(keyMap, lines, null)
    }

    keyMap.values.shouldContainExactly(
      KeyBinding(
        "ez", '{', PairOpenCloseAction(
          isTargetOpen = true,
          DelimPair('{', '}')
        )
      ),
    )

    """
      map ez { <pair hello {}>
    """.lines().let { lines ->
      shouldThrow<EzModeRcParser.ParseError> {
        EzModeRcParser.parse(keyMap, lines, null)
      }.cause.shouldNotBeNull().message.shouldBe(
        "first argument of `pair` must be open or close"
      )
    }

    """
      map ez [ <pair open [[]]>
    """.lines().let { lines ->
      shouldThrow<EzModeRcParser.ParseError> {
        EzModeRcParser.parse(keyMap, lines, null)
      }.cause.shouldNotBeNull().message.shouldBe(
        "pair argument must have 2 chars: [[]]"
      )
    }

    """
      map ez " <pair open "">
    """.lines().let { lines ->
      shouldThrow<EzModeRcParser.ParseError> {
        EzModeRcParser.parse(keyMap, lines, null)
      }.cause.shouldNotBeNull().message.shouldBe(
        "pair chars must be different: \"\""
      )
    }
  }
}
