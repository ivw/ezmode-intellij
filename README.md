![Build](https://github.com/ivw/ezmode-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/27497.svg)](https://plugins.jetbrains.com/plugin/27497)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/27497.svg)](https://plugins.jetbrains.com/plugin/27497)

EzMode is a plugin for [JetBrains IDEs](https://www.jetbrains.com/ides/) that adds the power of modal editing,
without the steep learning curve!

- **Fast**: edit with minimal keystrokes, without needing a mouse or Ctrl/Alt
- **Built for modern IDEs**: open tool windows, control git, navigate diffs, work with split windows
- **Intuitive**: easy to memorize keyboard layout that fits on a single sheet
- **Customizable**: map any character to any IDE action, and define new modes

Install the plugin (*Settings > Plugins > Marketplace > [EzMode](https://plugins.jetbrains.com/plugin/27497-ezmode)*)
and open the tutorial in your IDE!

[Demo](https://github.com/user-attachments/assets/9695bfb2-c1b6-4932-87b0-67ec47d6f5b4)

Default keyboard layout:

![Keyboard layout](KeyboardLayout.png)

*Image created with [Keyboard Layout Editor](https://www.keyboard-layout-editor.com/#/gists/921b61bce0466d1a2678bc081b256d29)*

All of these keys are customizable in your `.ezmoderc` file.

## AceJump

Install the [AceJump](https://github.com/acejump/AceJump) plugin as well
to enable easy jumping to any visible symbol, which is bound to `Space` in EzMode.
You may have to restart after installing AceJump due to a current issue in the IntelliJ PluginClassLoader.
