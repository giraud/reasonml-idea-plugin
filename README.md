# reasonml-idea-plugin
ReasonML language plugin for idea

![screenshot](webstorm.png)

## Status

:exclamation: This is a **work in progress**, the language has very basic - and partially implemented - functionalities.
You can look at the [todo list](TODO.md).

This code might change quite a lot or break in the future.

Known limitations:
- idea project must be created at sources root directory (you can't have sources in `somewhere/app/` and project files in `somewhere/project/`)
- bug: `reformatOnSave` clear undo actions: no undo after saving the file 

## Features

- Structured view
- Syntax highlighting
- Line commenting with ctrl-alt-l
- code folding
- pair braces matcher
- bucklescript compiler integration
- reformat on save (*) (by default, action is also mapped to `crtl alt shift R`)
- Type annotation (* merlin)
- Completion (* merlin)

_(*) See integration_

## Screenshots

Type annotations (linux only):

![type](docs/type.gif)

Reformat on save (Ctrl+s)

![refmt](docs/refmt.gif)

Bucklescript window

![bsb](docs/bsb.gif)

## Integration

Integration with reason tools need to be explicitly set with VM properties.

Note: To edit your `idea[64].vmoptions` you can do it from the console, 
or via the menu `help > Edit Custom VM Options`. 

### Linux

- Install [reason-cli](https://github.com/reasonml/reason-cli)
- Edit your `idea[64].vmoptions`
- Add the following properties:
```properties
-DreasonBsb=node_modules/bs-platform/bin/bsb.exe
-DreasonMerlin=<absolute path to node>/bin/ocamlmerlin
-DreasonRefmt=<absolute path to node>/bin/refmt
```

### Windows

- Add `bs-platform` to your project
- Edit your `idea[64].vmoptions`
- Add the following properties:
```properties
-DreasonBsb=node_modules/bs-platform/bin/bsb.exe
-DreasonRefmt=<absolute path to your project>/node_modules/bs-platform/bin/refmt.exe
```

## Development

This project is heavily inspired by :
- [custom language tutorial](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html) from intellij
- [ocaml language plugin](https://github.com/sidharthkuruvila/ocaml-ide) from sidharthkuruvila ( :+1: )
