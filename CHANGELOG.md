<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# ezmode-intellij Changelog

## [Unreleased]

## 2.0.0

### Breaking changes to default keys: 
- `r` / `R` is now used for removing lines instead of `m` / `M`
- `m` / `M` is now used for scrolling half a page down/up

To revert this change, add this to your `.ezmoderc`:
```
map ez r <idea Replace>
map ez R <idea ReplaceInPath>
map ez m <idea EditorDeleteLine>
map ez M <idea EditorDeleteLine><idea EditorUp>
```

### Other changes:
- Add more variables: `projectname`, `mode`, `key`, `space`, `tab`, `nl`
- `<write>` action: Support variables enclosed in `${}`
- `<writevar>` action: Removed in favor of `<write>`
- `E` (select line) will always switch to select mode, even if the line is empty

## 1.4.0
- Support XML/HTML tag jumping and selection
- `<pair>` action: support multiple delims
- `~` key: Unselect after toggling case
- Improve tutorial
- Change plugin description
- Improve KeyboardLayout.png

## 1.3.0
- Always scroll to caret in actions that change the selection
- Add more key mappings: `$`, `gP`, `gf`, `gm`, `7r`, `7R` 

## 1.2.0
- Git review action: when already reviewing, exit to source
- Add `special` mode
- Tab key: move ExpandLiveTemplateByTab to Alt-t
- Support changing mode anywhere in an action string
- Fix issue when using AceJump in select mode
- Add toggle case action to `~` key
- Add keystroke history view to cheat sheet
- Cheat sheet: print default key and space key again
- Jump to paired delimiter: fix delimiter counting when the caret is next to an opposite delim

## 1.1.0
- Restore IDE Tab shortcuts when unloading the plugin.
- Git mode improvements: add "review" action, and exit git mode when pressing `w`.
- .ezmoderc: Warn when changing mode at the end of an action.
- Improve "select line" action.
- The `[]` keys in select mode are now the same as in ez mode.
- Fixed and improved multiple cursor handling. Add `.` and `,` keys to select mode.

## 1.0.0
### Changed
- `.ezmoderc`: `caretcolor` is replaced with `primarycolor` and `secondarycolor`
- `.ezmoderc`: Renamed `toolbar` to `toolwindow`
- Improve `.ezmoderc` template
- Removed IDE version upper bound
- Let `git` inherit all keys of `ez` mode
- You can now use `e` to exit select mode, and `g` to exit git mode
- Allow jumping to `[]` and `<>` in select mode
- Cheat sheet: print keys in qwerty order

### Added
- Add secondary caret color for special modes, such as git mode
- Add "Got it" tooltip for git mode

## 0.0.2
### Added
- Initial release
