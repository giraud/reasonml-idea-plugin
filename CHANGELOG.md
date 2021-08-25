# Changelog

> **Tags:**
> - :boom:       [Breaking Change]
> - :rocket:     [New Feature]
> - :bug:        [Bug Fix]
> - :memo:       [Documentation]
> - :house:      [Internal]
> - :nail_care:  [Polish]

(_Tags are copied from [babel](https://github.com/babel/babel/blob/master/CHANGELOG.md)_)

Please note that **rescript development is paused** until issues in rescript tooling like 
[#4846](https://github.com/rescript-lang/rescript-compiler/issues/4846) and [#22](https://github.com/rescript-lang/rescript-vscode/issues/22)
are solved.

## Unreleased

- :bug: ![o] [#340](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/340) Items missing in structure panel after let ending with match (incorrect object parsing)
- :bug: ![o] [#329](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/329) IllegalArgumentException: Not an OCaml node: Element(OCAML_LAZY_NODE)

## 0.102.1 - 2021/07/06

- :bug: [#333](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/333) CLion crash, remove dependency to java only class
- :bug: [#157](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/157) Update parameter info handler, only show known types

0.102

- :house: [#328](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/328) Update Rescript parser to latest syntax
- :bug: [#326](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/326) Structure panel shows "mutable" for mutable record fields rather than the field name
- :bug: [#318](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/318) Rescript integration

## 0.101 - 2021/05/27

- :bug: [#303](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/303) Incorrect resolution for record field
- :house: Reference resolution algorithm has been totally redesigned

## 0.100 - 2021/05/05

- :house: move to jfrog, bintray is no more available 
- :bug: [#317](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/317) Belt.Array incorrectly resolved

# 0.99 - 2021/04/08

Version 0.99 supports the 2021.1 release.
 
- :bug: [#315](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/315) ![r] insertion handler for JSX doesn't detect close tag
- :bug: [#190](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/190) show nested function definitions in structure panel
- :bug: fix incorrect identification of optional parameter
- :nail_care: ![r] order jsx attributes, mandatory first

# 0.98.3 - 2021/04/06

- :bug: [#314](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/314) missing item in structure panel (structural diff operator)
- :bug: [#313](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/313) alphabetize opened/included modules in the structure panel

0.98.2

- :bug: [#312](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/312) ![o] switch with optional is not parsed correctly
- :bug: [#311](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/311) ![o] Missing icon in mli file
- :bug: [#310](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/310) ![o] Missing types in structure panel after class definition

0.98.1

- :bug: [#293](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/293) ![o] Better 'and' parsing
- :house: Better integration with yarn workspaces (v2?): search for node_modules in parent directory also

0.98

- :nail_care: ![o] highlight of ocaml annotation include arrobase
- :bug: [#309](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/309) incorrectly parse infix operators
- :bug: [#261](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/261) warning configuration in bsconfig.json is not being applied

# 0.97 - 2021/02/04

- :bug: [#304](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/304) ![r] Properties in component using external are not found
- :bug: Keep caret at position when reformat
- :house: Fix file pattern introduced in bs-platform 8.3
- :house: Fix reformatOnSave when multiple projects (different settings) are opened
- :house: Update ErrorAnnotator to use new ninja format introduced since bs-platform 8.4
- :house: Update ppx error extraction

## 0.96 - 2021/01/08

- :bug: [#295](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/295) ![o] Parser error: incorrect chaining module/type
- :bug: [#294](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/294) ![o] `let open in` incorrectly parsed
- :bug: [#291](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/291) ![o] 'function' not parsed as function
- :house: Better usage of externalAnnotator to highlight compilation problems in editor by [@pooch](https://github.com/JohnPucciarelli)
- :house: Better reformatOnSave implementation that should prevent infinite loop

## 0.95.1 - 2020/12/03

A brand new site created by John Pucciarelli (https://reasonml-editor.github.io/reasonml-idea-plugin/)

2020.3 support

- :rocket: [#284](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/284) ![o] Support for OCamlFormat
- :rocket: [#110](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/110) ![o]![r]![n] Language injection in strings
- :bug: [#279](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/279) ![n] Matched brace isn't highlighted in .res source file
- :bug: [#278](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/278) ![o] let (//) incorrectly parsed as comment
- :bug: [#277](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/277) Wrong module resolution for js object
- :nail_care: New plugin icon by [@pooch](https://github.com/JohnPucciarelli)

## 0.94 - 2020/11/03

Dune integration has been updated, [documentation](https://github.com/reasonml-editor/reasonml-idea-plugin/tree/master/docs) is also up to date. 

- :bug: [#276](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/276) Class JSFile not found
- :bug: [#275](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/275) Incorrect parsing of switch with signature
- :bug: [#245](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/245) Resolve property of JSX tag
- :bug: [#124](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/124) Repl freezes with Ocaml 4.07
- :nail_care: Fold switch/match
- :house: Use [rincewind@0.9.1](https://github.com/giraud/rincewind)
- :house: refactor Dune integration: works with native (linux), wsl (windows) and cygwin (windows)

## 0.93.1 - 2020/10/09

0.93.1

- :bug: [#272](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/272) Another case: structure panel missing function definition with "and"
- :nail_care: [#274](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/274) Omit '()' in structure panel
- :nail_care: [#273](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/273) Some constructor names not highlighted in function/match patterns

0.93

- :rocket: [#119](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/119) support for automatically closing quotes (double)
- :nail_care: 'Goto class' use a custom presentation for inner modules 
- :bug: [#271](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/271) Structure panel missing function defined with "and"
- :bug: [#270](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/270) Function definition with type variables not recognized as a function
- :bug: [#91](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/91) Jumping to some module-related symbol definitions from Structure Panel doesn't highlight symbol right away (incorrect module type detection)
- :bug: Fix monorepo on windows

## 0.92 - 2020/09/18

0.92.1

- :bug: [#269](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/269) Incorrect display of class methods names in structure panel
- :nail_care: Better parsing of React fragment <></>
- :nail_care: Better presentation of inner modules in 'goto class' dialog

0.92

This new version is compatible with 2020.2.
To support that version, the references have been redesigned. It might be needed to invalidate your caches and
maybe restart project from scratch (or remove .idea folder).
 
- :rocket: [#264](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/264) a new unified language settings by [@pooch](https://github.com/JohnPucciarelli)
- :rocket: [#81](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/81) Support GoTo/Implementation(s)
- :bug: [#266](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/266) Incompatible with 2020.2 IDEs
- :bug: [#268](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/268) Regression: Structure panel should group methods under the class name
- :bug: [#256](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/256) Probable regression for jumping to a symbol
- :house: Reworked parsers (try to remove some complexity) - rescript included [#260](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/260)
- :house: .res files are automatically compiled when saved, like .re

## 0.90.2 - 2020/07/02

0.90.1

This is a quick patch release to provide syntax highlighting to the new `.res` and `.resi` file types.
Don't expect anything more than just highlighting.

- :bug: Fix 'add braces' action when function has signature

0.90.0

- :memo: Reworked documentation, moved from wiki to the [docs](https://github.com/reasonml-editor/reasonml-idea-plugin/tree/master/docs) directory by [@pooch](https://github.com/JohnPucciarelli)
- :memo: [#252](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/252) Add an architecture documentation to explain some of the internal details
- :bug: [#247](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/247) Duplicated file templates
- :bug: [#205](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/205) Functors are now indexed (completion and resolution should work)
- :bug: [#254](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/254) ![r] Make a distinction between single-line and multi-line comments in parser
- :bug: [#254](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/254) ![r] Fold multiline JSX tags
- :house: Add github action for CI (and badges) by [@pooch](https://github.com/JohnPucciarelli)
- :house: [#253](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/253) Breaking apart modules to avoid extra instances of IntelliJ when testing by [@pooch](https://github.com/JohnPucciarelli)
- :house: [#257](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/257) Fix monorepo executable by [@Coobaha](https://github.com/Coobaha)

## 0.89.0 - 2020/05/22

- :rocket: [#239](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/239) Experimental: start of ESY support work by [@pooch](https://github.com/JohnPucciarelli)
- :bug: [#249](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/249) Too many warnings in log (bsconfig not found)
- :bug: [#241](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/241) Fix bug that prevent IDEA to keep correctly the source of SDK by [@kayl669](https://github.com/kayl669)
- :bug: [#236](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/236) Cannot find a module definition if namespace is used
- :house: Find real bsc/bsb binaries if a symlink is found
- :house: Fix gutter icons for navigation in OCaml files

## 0.88.1 - 2020/05/02

0.88.1
- :house: [#238](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/238) ![o] ![r] Add build system info to action labels by [@Coobaha](https://github.com/Coobaha)
- :house: [#234](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/234) Display warning instead of exception

0.88
- :rocket: ![o] You can easily download OCaml SDK sources in SDK platform settings
- :bug: [#231](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/231) Fix ClassCastException when reading bsconfig.json
- :nail_care: [#220](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/220) ![o] ![r] Added BuckleScript file type and icon by [@pooch](https://github.com/JohnPucciarelli) 
- :nail_care: [#219](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/219) ![o] ![r] Added file type support for Esy by [@pooch](https://github.com/JohnPucciarelli)
- :nail_care: ![o] ![r] Poly-variants can have a different highlighting, color scheme updated
- :nail_care: ![o] ![r] Use SVG icons
- :house: [#224](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/224) ![r] Use bsb.exe instead of node.js wrapper in monorepo by [@Coobaha](https://github.com/Coobaha)
- :house: [#221](https://github.com/reasonml-editor/reasonml-idea-plugin/pull/221) ![o] ![r] Compilers have separate tool windows by [@pooch](https://github.com/JohnPucciarelli) 
- :house: Use rincewind@0.8

## 0.87.1 - 2020/04/03

0.87.1
- :bug: [#214](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/214) Infinite indexing

0.87
- :rocket: Compatible with bucklescript 7.2.2
- :rocket: Add OCaml 4.10 to the list of downloadable SDKs
- :bug: [#211](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/211) `let%private` support
- :bug: [#209](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/209) Deep dependencies are not indexed in IDE that have javascript functionality (ultimate/webstorm)
- :house: Use rincewind 0.7 (support of OCaml 4.10)

## 0.86.1 - 2020/02/21

- :rocket: [#204](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/204) ![r] New on-the-fly compilation to get faster inferred information
- :bug: [#167](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/167) Go-to definition is not working correctly for deconstructed tuples
- :house: Compatible with bucklescript 7.2

## 0.85 - 2020/02/04

- :rocket: File | New menu as OCaml and ReasonML templates
- :rocket: Display cmt file in a custom editor
- :rocket: [#200](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/200) OCaml SDK can be set at the module level
- :house: Fix bsc path
- :house: New indexing of file modules
- :house: Use Rincewind 0.6
- :house: [#199](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/199) Prepare for dynamic plugins
- :house: [#196](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/196) Redesign content root finding

## 0.84 - 2019/12/10

- :bug: [#197](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/197) java.lang.Throwable: Assertion failed: Undoable actions allowed inside commands only
- :bug: [#192](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/192) Undoing causes Following files have changes that cannot be undone: error
- :house: Fix a stub exception for functor parameter

## 0.83 - 2019/12/03

New versioning scheme: each intellij release has its own plugin version.
For example, 2019.3 can only use plugin 0.83-2019.3. 

This is much more work when developing the plugin but it is needed because API changes between releases
can have many incompatibilities. The consequence is that I won't be able to maintain too 
many versions active. Older version of the plugin will be frozen and won't get any updates. 

## [0.82] - 2019/11/15

- :rocket: Importing a Dune project automatically configure project structure : SDK, module and project.
  SDK can be automatically downloaded and indexed.
- :rocket: Dune stanzas visible in the structure view
- :rocket: Dune stanzas can be folded when they span on multiple lines
- :bug: [#189](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/189) ![o] Structure panel missing members/parse problem for `while`
- :bug: [#175](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/175) ![o] Structure panel missing top level functions defined after nested `let foo = function`
- :house: Fix bs version extractor (format changed in bucklescript 6.2.1)
- :house: Improved Dune parser

## [0.81] - 2019/10/17

- :bug: [#185](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/185) `gradle verifyPlugin` fails
- :bug: [#156](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/156) Assigning any value to new variable confuses plugin
- :bug: [#88](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/88) ![r] ![o] GoTo/Declaration not looking for locally-defined symbol
- :bug: [#83](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/83) ![r] ![o] Support GoTo/Declaration for function-local symbols
- :nail_care: Try to improve quick navigation doc
- :house: ![o] Parsing directives (`#if`, `#else`, `#end`)
- :house: Get inferred type from definition if not found in usage
- :house: Some deduplication of pervasives expressions in free completion contributor
- :house: Work on signature conversion (partial conversion)
- :house: Better dot completion when module is an alias (ex: Belt.Map.String.<caret&gt;)

## [0.80] - 2019/09/23

- :bug: [#155](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/155) ![r] Quick documentation is not showing inside parenthesis
- :bug: [#133](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/133) Syntax error popup is truncated
- :bug: [#27](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/27) ![o] Reworked the uncommenter
- :nail_care: ![r] Better function folding
- :nail_care: ![r] Better parsing/highlighting of js template string
- :nail_care: Remove custom syntax highlighting for `options` in default style settings
- :house: rework Bs output listener, add test

## [0.79] - 2019/08/28

- :bug: [#176](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/176) Functions missing in structure panel after "while+match" code
- :bug: [#170](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/170) Functions in struct in "module : sig ... end = struct ... end" not shown in Structure panel
- :rocket: Add line marker for inner module
- :nail_care: Don't annotate interface file
- :house: Improve module resolution when it's only found in implementation file
- :house: Better dot completion for inner module in impl file
- :house: Remove the perf warning for LineMarker

## 0.78.4 - 2019/08/17

- :bug: [#177](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/177) Exception: "Access is allowed from event dispatch thread only."
- :bug: [#172](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/172) 2019.2 error
- :house: Try to look up bsb and refmt in node_modules/.bin dir to improve support of monorepo. Thanks to [@Coobaha](https://github.com/Coobaha)

## 0.78.2 - 2019/07/02

- :bug: [#173](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/173) Fixed exception in Webstorm: gutter icons are only available in java-based IDE
- :bug: [#172](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/172) Fixed exceptions in 2019.2 EAP
- :house: Jetbrains recommendation: moving project components to service components. Expected faster startup

## 0.78.1 - 2019/06/24

- :nail_care: Quick doc improvement on resolved elements for ReasonML (ctrl+hover)
- :house: Some type conversion work started (partial implementation)
- :house: Resolving variants with locally open path
- :house: Index and resolve record fields
- :house: Rincewind no more downloaded if already present

## 0.77 - 2019/06/05

- :bug: [#166](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/166) Indexing of Js.t
- :house: ![r] Some quick doc improvement on resolved elements for ReasonML (ctrl+hover)

## 0.76 - 2019/04/24

- :rocket: Support for ocaml 4.0.6 (Bs 6+)
- :bug: [#165](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/165) Autocompletion inside comments
- :bug: [#161](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/161) Indexing exception
- :bug: [#160](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/160) Autocompletion is not using module interface

## 0.75.1 - 2019/04/16

- :bug: [#154](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/154) Remove unwanted end of line after reformat
- :bug: [#153](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/153) Split tuple items in structure view for let expressions
- :bug: [#152](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/152) Add a check to prevent exception
- :house: Fix alpha sort on open/include in structure view
- :house: Improved OCaml parser
- :house: refmt is set to 80 cols by default
- :house: add missing file

## 0.74 - 2019/03/25

- :rocket: ODoc formatting when displaying special comments (ctrl+q), with the following limitation: element must be resolved, and only a subset of syntax is supported
- :bug: [#153](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/153) Structure panel for let fn and let (..)
- :bug: [#151](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/151) Fix parameters hints (is empty)
- :bug: [#150](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/150) Include expressions from include keyword
- :bug: [#148](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/148) Don't auto-reformat ml/mli files
- :house: Redesign completion provider
- :house: Upgraded minimal supported version of intellij product to 173

## 0.73 - 2019/03/12

- :rocket: Implement a (basic) related line marker
- :rocket: Introduce a new OCaml module
- :nail_care: Display a message at the end of the compilation
- :bug: [#149](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/149) ![o] ReasonML confused by .mly (yacc) files; structure panel shows most of the grammar on one very long line
- :house: A new file based index
- :house: Remove basic OCaml types detection in lexer

## 0.72 - 2019/02/01

- :rocket: Highlight of dune file
- :rocket: Add a checkbox in Reason settings to disable bucklescript
- :nail_care: Display module path in 'go to class' popup
- :bug: [#126](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/126) ![r] Function parameter info attempts to show return type
- :bug: [#122](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/122) Make error locations in bucklescript tool window clickable
- :house: Use rincewind 0.4

## 0.71 - 2019/01/23

- :bug: [#136](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/121) ![r] ![o] Unicode characters inside strings turn into question marks
- :bug: [#135](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/135) ![o] Top-level declaration following "let f = function ... and" doesn't appear in the structure panel
- :bug: [#109](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/109) ![r] ![o] Reformat interface
- :house: 2017.2 is the minimal idea version supported

## 0.70 - 2019/01/18

- :rocket: Handle single line comments in Reason files
- :rocket: Completion on open/include
- :rocket: Live templates for reason-react
- :rocket: Naive navigation between code/test modules (must end by _test or _spec)
- :nail_care: Add functor icon
- :nail_care: Reworked files icons
            
## 0.69.1 - 2018/12/17

- :rocket: Automatically adds parenthesis for 'Some'
- :rocket: add basic highlighting for .mll and .mly
- :bug: Fix record field parsing when multiple annotations are used
- :house: Better completion (variants, signatures)
- :bug: [#121](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/121) ![r] ![o] Show Functor structure
- :bug: [#120](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/120) ![r] Incorrect highlighting of JSX being a function parameter

## 0.67.2 - 2018/11/16

- :house: ![r] Improve JSX parsing
- :bug: [#116](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/116) ![o] Structure panel: show items defined in modules
- :bug: [#105](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/105) ![o] Missing/incorrect structure info for function named "string"

## 0.67.1 - 2018/11/10

- :bug: [#115](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/115) ![o] "end" highlighted the same way as an unmatched parenthesis
- :bug: [#113](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/113) ![r] Function parsing is not correct

## 0.67 - 2018/10/27

- :rocket: [#112](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/112) Build hotkeys
- :bug: [#111](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/111) Bsb window actions are not working outside of reason file
- :bug: [#108](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/108) Reason language id in markdown preview
- :bug: [#106](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/106) Inconsistent highlighting of matching (), [] and {}

## 0.66.2 - 2018/10/23

- :house: Deleted virtual file listener because they are shared between projects. Now using editor listeners.
- :house: Reworked the compilers code

## 0.65.1 - 2018/10/18

- :bug: Dune file were no more editable because of a change in the parser
- :rocket: ![r] Parameter info (when possible) using ctrl-p
- :rocket: [#97](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/97) ![r] ![o] Support for rtop / RPEL as tool window
- :house: ![r] JSX completion can find inner component modules
- :house: improve parser (function parameters, variant constructor, signature)
- :bug: Transform compiler and refmt to project component, to fix incorrectly shared confs
 
## 0.64 - 2018/10/08

- :rocket: ![r] Js language injection in [%raw] blocks
- :nail_care: ![r] Better annotation parsing
- :nail_care: ![r] ![o] Add missing keywords in highlighter
- :bug: [#67](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/67) Curly braces intentions are broken

## 0.63 - 2018/09/20

- :nail_care: [#98](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/98) Add dates to changelog
- :bug: Renaming inner module should work
- :bug: [#99](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/99) ![r] ![o] Incorrect syntax highlighting when a comment contains "*)" (including the quotes)
- :bug: [#95](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/95) ![r] ![o] Class names don't appear in Structure panel
- :bug: Fix a bug that prevent plugin to work with 2018.3-EAP

## 0.62.1 - 2018/09/06

- :rocket: Add a 'make' button to the bs console
- :bug: [#62](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/62) ![o] Awkward behavior when typing comments
- :bug: [#27](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/27) Uncommenting nested comments is incorrect

## 0.61 - 2018/08/29

- :nail_care: [#92](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/92) Changing icons
- :bug: Fixed a bug that prevented 'find usages' to work on lower symbols
- :bug: [#89](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/89) Renaming variables doesn't work
- :bug: [#94](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/94) NPE in findBaseRootFromFile
           
## 0.60.2 - 2018/07/31

- :nail_care: [#59](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/59) Colored output in Bucklescript tab
- :bug: [#90](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/90) IllegalArgumentException when clicking on an entry in "Structures" to go to a symbol
- :house: Parsers updated

## 0.59.1 - 2018/07/25

- :rocket: You can use 'Go to class' to find OCaml/ReasonMl modules
- :bug: [#78](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/78) ![o] Let symbol missing in structure panel
- :bug: [#82](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/82) Exception for GoTo/Declaration of a function parameter
- :bug: [#87](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/87) NullPointerException at jpsplugin.com.reason.Platform.findBaseRootFromFile

## 0.59 - 2018/07/19

- :rocket: [#71](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/71) Monorepo with multiple bs projects
- :nail_care: ![o] 'let _' is not shown in the structure panel
- :bug: Fix (again) property completion in JSX after rework on references has been done

## 0.58.1 - 2018/07/18

- :bug: [#79](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/79) IDE crash
- :bug: [#77](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/77) Show non-alphanumeric function names such as ">>=" in the structure panel

## 0.58 - 2018/07/12

This release contains a big rewrite of how modules are referenced, it may break things that were working before (ex: find usages).

- :bug: [#72](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/72) Autocomplete not working in uncurried function

## 0.57.1 - 2018/07/10

- :rocket: Some record field completion
- :bug: [#66](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/66) Go To + module alias
- :house: ![o] Better module path resolution
- :house: Improved parsers
            
## 0.56

- :rocket: ![r] Go to symbol is working for external/let mainly
- :rocket: ![r] ctrl-hover on a lower symbol display its type (still wip, not working for all symbols)
- :bug: ![r] JSX attribute completion with reason-react 0.4.2 (from external to type)

## 0.55.1

* :house: fix editor freeze when too many cmt/i files are updated
* :house: ![o] better completion

## 0.54

* :rocket: add "exposing" code lens to opens
* :house: completion uses opens and local opens
* :house: better path resolution for "go to" action
* :house: rincewind-0.2
* :house: improve parsers

## 0.53

* :rocket: ![r] ![o] improve parsers
* :rocket: ![r] improve braces intention
* :bug: [#65](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/65) refmt not working in .rei

## 0.52

* :rocket: ![o] Pair match in OCaml: struct/end and sig/end
* :rocket: [#53](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/53) ![r] Implemented intentions
* :nail_care: Add bool/char/int/float/string as keywords
* :nail_care: [#64](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/53) Handle warning message from bsb
* :house: Protect against concurrent run of bsb compilation process
            
## 0.51.1

* :bug: Fix [#61](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/61)
* :house: Slightly better bootstrap for type inference

## 0.51

* :bug: [#52](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/52) Fix problem when parsing JSX tag names
* :house: Type annotation is now using a native cmt extractor (Windows, Linux, OSX)

## 0.50

* :nail_care: [#55](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/55) Sorry, no more facets: they can't be used outside Idea. Settings can be found in Project settings and they are per project

## 0.49

* :rocket: [#47](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/47) No more JVM properties 'reasonBsb' and 'reasonRefmt', configuration is done via facet

## 0.48.1

* :bug: [#51](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/51) Fix a NPE in OCaml

## 0.48

* :house: Fix folding code
* :house: Better locate js.ml, belt.ml
* :house: Add Pervasives to completion
            
## 0.47.1

* :bug: Fixed 2 bugs (stack and 'editor not disposed' exceptions)
* :house: Fixed recursive type parsing problem with variants

## 0.47

* :rocket: Reason settings to change reformat column width
* :bug: [#45](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/45) (classes have been deleted)
* :house: Better error extraction
* :house: Reformat on save can be activated (see doc)

## 0.46

* :bug: Fix version 0.45
* :rocket: Better error annotation (includes warnings)
        
## 0.45 

* :nail_care: Bucklescript window has an icon
* :nail_care: File icons updated
* :bug: Goto action use the same path resolution than completion
* :rocket: Display signature for 'val' in completion popup
* :house: Parsers improvement
* :house: Reworked how to get the types from cmi files

## 0.44

* :bug: [#40](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/40)
* :house: Improve completion on expressions

## 0.43

* :bug: [#35](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/35) Type annotations not working with bs namespace
* :house: Improve completion on expressions

## 0.42

* :rocket: Add |. operator
* :rocket: goto action on file module is working
* :house: Improve completion on expressions
* :bug: Color settings no more blocked on loading spinner

## 0.41

* :house: Much improved performances for code lens

## 0.40

* :rocket: JSX completion (tag/attribute)
* :rocket: Variant names can be highlighted
* :house: Improved parsers
         
## 0.39

* :house: Better JSX parsing/highlighting
* :rocket: Code lens style is customisable
* :bug: [#33](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/33)
* :rocket: Completion contributor for OCaml files (wip)

## 0.38

* :house: Better JSX parsing/highlighting
* :house: Fix inferred type annotations
* :house: Improved reason parser

## 0.37

* :bug: [#30](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/30)
* :rocket: add completion when starting typing
* :house: Display types for val/external (completion popup)
* :house: Use mli file when present (Pervasives)

## 0.36

* :bug: [#29](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/29)
* :house: Improve parsers
* :rocket: add keyword completion (start expression)
* :rocket: add module completion for open expression

## 0.35

* :bug: Fix an infinite loop in PsiModule
* :house: Improve parsers 

## 0.34

* :bug: Fix a NPE in the folder code when comments are too small
* :bug: [#28](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/28) IntelliJ error "Assertion failed: Too many element types registered. Out of (short) range." **maybe**
* :rocket: [#25](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/25) Support .ml4 files 

## 0.33

* :bug: [#22](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/22) Mishandling of "Comment with Block Comment" action (CTL-SHIFT-/)
* :bug: [#21](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/21) Error message: "startNotify called already" 

## 0.32

* :nail_care: [#20](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/20) Improved notification, add plugin name and link to github 
* :rocket: 'go to' for Open expression (ctrl+click)
* :bug: [#19](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/19) Character constant with an octal character not highlighted correctly 

## 0.31

* :nail_care: val expressions (OCaml) are displayed in the structure view
* :nail_care: Exceptions are displayed in the structure view
* :bug: [#18](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/18) Incorrect handling of characters in parser 
* :house: Improve OCaml parser

## 0.30

* :house: Fix Idea 15 compatibility
* :house: Work done on indexing and referencing (module)

## 0.29

* :house: Redesigned the reason parser

## 0.28

* :rocket: Reformat action is working on ocaml source
* :house: Working on stub indexing and first implementation of completion using static analysis

## 0.27

* :bug: Fixed an IllegalArgumentException in KillableColoredProcessHandler
* :rocket: Structure view enabled for OCaml files
* :rocket: Inferring types using bsc if no merlin found (wip)
* :house: Using gradle for build
* :house: Improving reason parser

## 0.26

* :house: Improving parsers

## 0.25

* :rocket: [#14](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/14) Add a very lightweight support of OCaml, to have more syntax coloring and no incorrect parser errors

## 0.24

* :nail_care: [#13](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/13) Add Number and Type argument in color settings

## 0.23

* :house: New default values for bsb and refmt. bsb=> node_modules/bs-platform/bin/bsb.exe, refmt=> node_modules/bs-platform/bin/refmt3.exe

## 0.22

* :rocket: Parse Bsb super errors and use them to annotate source code

[0.82]: https://github.com/reasonml-editor/reasonml-idea-plugin/compare/0.81...0.82
[0.81]: https://github.com/reasonml-editor/reasonml-idea-plugin/compare/0.80...0.81
[0.80]: https://github.com/reasonml-editor/reasonml-idea-plugin/compare/0.79...0.80
[0.79]: https://github.com/reasonml-editor/reasonml-idea-plugin/compare/v0.78.3...0.79
[r]: website/static/img/reason-file.png
[o]: website/static/img/ocaml-file.png
[n]: website/static/img/rescript-file.png
