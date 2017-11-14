# reasonml-idea-plugin
ReasonML language plugin for idea

![screenshot](screenshot.png)

## Status

:exclamation: This is a **work in progress**, the language has very basic - and partially implemented - functionalities.
You can look at the [todo list](TODO.md).

This code might change quite a lot or break in the future.

Known limitations:
- idea project must be created at sources root directory (you can't have sources in `somewhere/app/` and project files in `somewhere/project/`)
- if using merlin, then merlin 3+ is required

## Features

- Structured view
- Syntax highlighting
- Line commenting with ctrl-alt-l
- code folding
- pair braces matcher
- bucklescript compiler integration
- reformat (by default, action is also mapped to `crtl alt shift R`)
- Type annotation (* merlin)
- Completion (* merlin)

_(*) See integration_

This plugin offer a lightweight integration for Ocaml files, mainly syntax highlighting.

## Screenshots

Type annotations (linux only):

![type](docs/type.gif)

Reformat using refmt(3)

![refmt](docs/refmt.gif)

Bucklescript window

![bsb](docs/bsb.gif)

## Integration

Note: To edit your `idea[64].vmoptions` you can do it from the console, 
or via the menu `help > Edit Custom VM Options`. 

### Linux

- Install `reason.1.13.7` and `merlin.3.0.2` using opam (you can't use reason-cli because merlin 3+ is required)
- Edit your `idea[64].vmoptions`
- Add the following properties:
```properties
-DreasonBsb=node_modules/bs-platform/bin/bsb.exe
-DreasonMerlin=<absolute path to opam>/bin/ocamlmerlin
-DreasonRefmt=<absolute path to opam>/bin/refmt
```

### Windows

- Add `bs-platform` to your project
- If `bs-platform` is installed locally (defined in your `package.json`), then you should have nothing else to do
- If `bs-platform` is installed globally:
-- Edit your `idea[64].vmoptions`
-- Add the following properties:
```properties
-DreasonBsb=<absolute_path>/bsb.exe
-DreasonRefmt=<absolute_path>/refmt3.exe
```

## Development

This project is heavily inspired by :
- [custom language tutorial](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html) from intellij
- [ocaml language plugin](https://github.com/sidharthkuruvila/ocaml-ide) from sidharthkuruvila ( :+1: )
