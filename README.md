![Build](https://github.com/ivw/ezmode-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/27497.svg)](https://plugins.jetbrains.com/plugin/27497)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/27497.svg)](https://plugins.jetbrains.com/plugin/27497)

EzMode is a plugin for [JetBrains IDEs](https://www.jetbrains.com/ides/) that brings the power of modal editing, without
the steep learning curve.

- **Fast**: edit with minimal keystrokes, without needing a mouse or Ctrl/Alt
- **Built for modern IDEs**: open tool windows, control git, navigate diffs, manage split windows
- **Intuitive**: easy-to-memorize keyboard layout that fits on a single sheet
- **Customizable**: map any character to any IDE action, and define new modes

[Demo](https://github.com/user-attachments/assets/9695bfb2-c1b6-4932-87b0-67ec47d6f5b4)

### Default keyboard layout:

![Keyboard layout](KeyboardLayout.png)
*View on [Keyboard Layout Editor](https://www.keyboard-layout-editor.com/#/gists/921b61bce0466d1a2678bc081b256d29)*

---

## Getting Started

1. Open the plugin marketplace in your IDE: *Settings > Plugins > Marketplace*
2. (Optional but recommended) Install the [AceJump](https://github.com/acejump/AceJump) plugin.
3. Install the [EzMode](https://plugins.jetbrains.com/plugin/27497-ezmode) plugin.
4. Open the [tutorial](src/main/resources/com/github/ivw/ezmode/actions/tutorial.md) in your IDE by clicking the mode
   indicator in the bottom-right corner.

## Customization

The full keymap is defined in [base.ezmoderc](src/main/resources/com/github/ivw/ezmode/config/base.ezmoderc),
which you can override with your own `.ezmoderc` file.

Key mappings use this format:

```
map {mode} {keychar} {actions}
```

### `mode`

The mode in which the key mapping is active. Built-in modes include `ez`, `type`, `select`, and `git`, but you can
define your own as well.

### `keychar`

The character that has to be "typed" to trigger the action. Naturally, upper case means you have to hold shift.

Ctrl/Alt shortcuts are not characters and not handled by EzMode.

Special values:

- `<space>`: the space character.
- `<default>`: the default key mapping, which will be triggered by any key that does not have a mapping for the given
  mode.

### `actions`

A string of one or more actions with no separators.
You can map an action to the parent keymap by typing its character,
or you can use a base action:

- `<idea SomeActionId>`: Invoke an IntelliJ IDE action. Most action IDs can be
  found [here](https://github.com/JetBrains/intellij-community/blob/master/platform/platform-resources/src/keymaps/%24default.xml)
- `<mode somemode>`: Switch to a different mode
- `<ofmode somemode>`: Let another mode handle the character
- `<native>`: Insert the character into the editor
- `<write Hello word!>`: Insert a string into the editor
- `<toolwindow ToolWindowId>`: Toggle a tool window (list of
  IDS [here](https://github.com/JetBrains/intellij-community/blob/master/platform/ide-core/src/com/intellij/openapi/wm/ToolWindowId.java))

### Examples

Map `C` (Shift + c) in `ez` mode to select all (`A`) and copy (`c`):
```
map ez C Ac
```

Create a mode that types every character twice:
```
map doubletype <default> <native><native>
map ez X <mode doubletype>
```

More practical examples can be found in
the [template .ezmoderc](src/main/resources/com/github/ivw/ezmode/config/template.ezmoderc)
