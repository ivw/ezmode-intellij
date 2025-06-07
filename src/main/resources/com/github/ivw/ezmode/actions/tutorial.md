# EzMode Tutorial

Welcome to EzMode - fast and intuitive modal editing for modern IDEs.

To enter EzMode, press `Tab`. The caret will turn orange,
and the mode indicator in the bottom-right corner will show "ez".
In EzMode, character keys are mapped to actions. Ctrl/Alt shortcuts still work normally.

To return to typing, press `t`. In type mode, everything behaves as usual.

I recommend using EzMode as the default mode: enter type mode only
when you need to insert text, and press Tab when you're done typing.
Once you're used to it, you can edit quickly without moving your hands off the home row.


## Moving the caret

Use `i`, `j`, `k`, `l` to move, just like the arrow keys.

To move up and down by paragraph, use uppercase `I` and `K`.
Holding one of these keys is a good way to scroll through a document.

`J` / `L`: Move backward/forward in history
`u` / `o`: Move backward/forward by word
`h` / `;`: Move to the start/end of a line
`H` / `:`: Move to the start/end of a file

*Make sure you have the AceJump plugin installed for this step:*
To jump to any visible symbol, press `Space`,
then type one or more of the characters you want to jump to,
and type the adjacent tag.


## Selecting text

Press `e` to enter select mode.
Now use the movement keys (see above) to extend the selection.
Press `c` to copy, `d` to delete, `t` to type, or Tab to cancel selection.

Other ways to enter select mode:
`E`: Select line
`a`: Select word
`A`: Select all

```
Exercise: change THIS_WORD to 123.
```

```
The last part of this sentence SHOULDN'T BE UPPER CASE.
```
*Hint: use `P` to open the command palette, from which you can toggle case.*


## Basic actions

`z` / `y`: Undo/redo
`x`: Cut
`c`: Copy
`v`: Paste
`s`: Save
`f` / `r`: Find/replace
`/`: Comment line

You probably already know these - they match common Ctrl shortcuts!

To view the full keymap, press `5`.

```
Exercise: Copy this line and paste it 3 more times.
```
*Hint: To copy a line, you don't need to select it. Simply press `c`.*


## Brackets and quotes

Type any bracket or quote character (`{} () [] '' ""`) to jump to the nearest surrounding one.
With the caret positioned there:
`-`: Select inside the brackets or quotes
`=`: Select around the brackets or quotes
`_`: Remove the brackets or quotes and select the contents

```
exercise(remove, these, arguments)
```

```
exercise + (remove + parentheses)
```

```
exercise('Change the quotes to double quotes')
```
*Hint: After removing the quotes, use `T"` to surround them with double quotes.*


## Multiple carets

Press `0` to add the next occurrence of the current selection to the selection.
Press `9` to undo the last added selection.

```
Exercise: Change every 0 to a 1: 0_0_0_0_0
```

```
Exercise: Remove the number next to every x: x0 x1 x2 x3 x4
```
*Hint: Press Tab to exit select mode while keeping the multiple carets.*

When in select mode (`e`), use `.` / `,` to add a new caret below/above.

```
Exercise: Add a dash to the start of these 3 sentences.
Including this one.
And this one.
```

## Files and windows

Use `p` to open any file. Use `w` to close a tab or `W` to close all other tabs.

Use `\` to split the editor into two windows,
or `|` to do the same but without the copy on the left.
Use `q` to move between split windows and `Q` to close a split window.

Toggle tool windows with the lower number keys:
`1`: File tree, `2`: Local changes, `3`: Terminal.
To focus a tool window, use the above keys with shift. Press `Esc` to unfocus.
Use `4` to hide all tool windows.


## Customization

In EzMode, every character key is customizable. You can even define your own modes.
Open your config file by typing `<` in EzMode, and choosing "Open .ezmoderc".
This will create a template with some examples if the file doesn't already exist.
After editing and saving this file, use `<` again and
choose "Reload .ezmoderc" to apply the changes.

Tab is the default key to enter EzMode because it's easy and not often used
in modern IDEs with automatic indentation. The original Tab functionality is moved to `Alt-T`.
You can change any of this in *Settings > Keymap*.
