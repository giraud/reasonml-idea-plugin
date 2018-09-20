# Changelog

> **Tags:**
> - :boom:       [Breaking Change]
> - :rocket:     [New Feature]
> - :bug:        [Bug Fix]
> - :memo:       [Documentation]
> - :house:      [Internal]
> - :nail_care:  [Polish]

(_Tags are copied from [babel](https://github.com/babel/babel/blob/master/CHANGELOG.md)_)

## 0.63 - 2018/09/20

- :nail_care: [#98](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/98) - Add dates to changelog
- :bug: Renaming inner module should work
- :bug: ![r](resources/icons/reason-file.png) ![o](resources/icons/ocaml-file.png) [#99](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/99) - Incorrect syntax highlighting when a comment contains "*)" (including the quotes)
- :bug: ![r](resources/icons/reason-file.png) ![o](resources/icons/ocaml-file.png) [#95](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/95) - Class names don't appear in Structure panel
- :bug: Fix a bug that prevent plugin to work with 2018.3-EAP

## 0.62.1 - 2018/09/06

- :rocket: Add a 'make' button to the bs console</li>
- :bug: ![o](resources/icons/ocaml-file.png) [#62](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/62) - Awkward behavior when typing comments
- :bug: [#27](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/27) - Uncommenting nested comments is incorrect

## 0.61 - 2018/08/29

- :nail_care: [#92](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/92) Changing icons
- :bug: Fixed a bug that prevented 'find usages' to work on lower symbols
- :bug: [#89](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/89) Renaming variables doesn't work
- :bug: [#94](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/94) NPE in findBaseRootFromFile
           
## 0.60.2 - 2018/07/31

- :nail_care: [#59](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/59) Colored output in Bucklescript tab
- :bug: [#90](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/90) IllegalArgumentException when clicking on an entry in "Structures" to go to a symbol</li>
- :house: Parsers updated

## 0.59.1 - 2018/07/25

- :rocket: You can use 'Go to class' to find OCaml/ReasonMl modules
- :bug: ![o](resources/icons/ocaml-file.png) [#78](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/78) Let symbol missing in structure panel
- :bug: [#82](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/82) Exception for GoTo/Declaration of a function parameter
- :bug: [#87](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/87) NullPointerException at com.reason.Platform.findBaseRootFromFile

## 0.59 - 2018/07/19

- :rocket: [#71](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/71) Monorepo with multiple bs projects
- :nail_care: ![o](resources/icons/ocaml-file.png) 'let _' is not shown in the structure panel
- :bug: Fix (again) property completion in JSX after rework on references has been done

## 0.58.1 - 2018/07/18

- :bug: [#79](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/79) - IDE crash
- :bug: [#77](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/77) - Show non-alphanumeric function names such as ">>=" in the structure panel

## 0.58 - 2018/07/12

This release contains a big rewrite of how modules are referenced, it may break things that were working before (ex: find usages).

- :bug: [#72](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/72) - Autocomplete not working in uncurried function

## 0.57.1 - 2018/07/10

- :rocket: Some record field completion
- :bug: [#66](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/66) - Go To + module alias
- :house: ![o](resources/icons/ocaml-file.png) Better module path resolution</li>
- :house: Improved parsers
            
## 0.56

- :rocket: ![r](resources/icons/reason-file.png) Go to symbol is working for external/let mainly
- :rocket: ![r](resources/icons/reason-file.png) ctrl-hover on a lower symbol display its type (still wip, not working for all symbols)
- :bug: ![r](resources/icons/reason-file.png) JSX attribute completion with reason-react 0.4.2 (from external to type)

## 0.55.1

* :house: fix editor freeze when too many cmt/i files are updated
* :house: ![o](resources/icons/ocaml-file.png) better completion

## 0.54

* :rocket: add "exposing" code lens to opens
* :house: completion uses opens and local opens
* :house: better path resolution for "go to" action
* :house: rincewind-0.2
* :house: improve parsers

## 0.53

* :rocket: ![r](resources/icons/reason-file.png) ![o](resources/icons/ocaml-file.png) improve parsers
* :rocket: ![r](resources/icons/reason-file.png) improve braces intention
* :bug: [#65](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/65) refmt not working in .rei

## 0.52

* :rocket: ![o](resources/icons/ocaml-file.png) Pair match in OCaml: struct/end and sig/end
* :rocket: ![r](resources/icons/reason-file.png) [#53](https://github.com/reasonml-editor/reasonml-idea-plugin/issues/53) Implemented intentions
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
* :bug: Fix #45 (classes have been deleted)
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

* :bug: Fix #40
* :house: Improve completion on expressions

## 0.43

* :bug: Fix 'Type annotations not working with bs namespace' (#35)
* :house: Improve completion on expressions

## 0.42

* :rocket: Add |. operator
* :rocket: goto action on file module is working
* :house: Improve completion on expressions
* :bug: Color settings no more bloked on loading spinner

## 0.41

* :house: Much improved performances for code lens

## 0.40

* :rocket: JSX completion (tag/attribute)
* :rocket: Variant names can be highlighted
* :house: Improved parsers
         
## 0.39

* :house: Better JSX parsing/highlighting
* :rocket: Code lens style is customisable
* :bug: Fix #33
* :rocket: Completion contributor for OCaml files (wip)

## 0.38

* :house: Better JSX parsing/highlighting
* :house: Fix inferred type annotations
* :house: Improved reason parser

## 0.37

* :bug: Fix #30
* :rocket: add completion when starting typing
* :house: Display types for val/external (completion popup)
* :house: Use mli file when present (pervasives)

## 0.36

* :bug: Fix #29
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

* :bug: Fixed an IllegalArgumentException in KillableColoredProcessHandler</li>
* :rocket: 
  * Structure view enabled for OCaml files
  * Inferring types using bsc if no merlin found (wip)
* :house: 
  * Using gradle for build
  * Improving reason parser

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
