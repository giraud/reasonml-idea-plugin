# reasonml-idea-plugin
ReasonML language plugin for idea

![screenshot](screenshot.png)

## Status

:exclamation: This is a **work in progress**, the language has very basic - and partially implemented - functionalities.
You can look at the [todo list](TODO.md).

This code might change quite a lot or break in the future.

Known limitations:
- idea project must be created at sources root directory (you can't have sources in `somewhere/app/` and project files in `somewhere/project/`)

## Features

- Structured view
- Syntax highlighting
- Line commenting with ctrl-alt-l
- code folding
- pair braces matcher
- bucklescript compiler integration
- reformat (by default, action is also mapped to `crtl alt shift R`)
- Type annotation (* bs)

_(*) See integration_

This plugin offer a lightweight integration for Ocaml files.

## Screenshots

Type annotations:

![type](docs/type.gif)

Reformat using refmt(3)

![refmt](docs/refmt.gif)

Bucklescript window

![bsb](docs/bsb.gif)

## Bucklescript

Advanced features, like syntax error highlighting or type annotations require the installation of [Bucklescript](https://bucklescript.github.io/).

Bucklescript has an excellent support of Windows. 

Note: To edit your `idea[64].vmoptions` you can do it from the console, 
or via the menu `help > Edit Custom VM Options`. 

### Local installation

- Install bucklescript locally to your project using npm or yarn
- Start idea 

The plugin will find and use the binaries found in the `node_modules/bs-platform` directory.

### Global installation

- Install bucklescript globally
- Edit your `idea[64].vmoptions`
- Add the following properties:
```properties
-DreasonBsb=<absolute_path>/bsb.exe
-DreasonRefmt=<absolute_path>/refmt3.exe
```
- Start idea

The plugin will use the binaries defined in the properties.

## Development

This project is heavily inspired by :
- [custom language tutorial](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html) from intellij
- [ocaml language plugin](https://github.com/sidharthkuruvila/ocaml-ide) from sidharthkuruvila ( :+1: )
