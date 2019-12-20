# reasonml-idea-plugin
ReasonML language plugin for idea

![screenshot](screenshot.png)

## Documentation

Please see the [wiki](../../wiki).

## Features

<div style="float:left">
    <div>Reason&nbsp;syntax</div><img src="docs/syntax-reason.png"/>
</div>
<div style="float:left">
    <div>Ocaml&nbsp;syntax</div><img src="docs/syntax-ocaml.png"/>
</div>
<div style="float:left">
    <div>Dune&nbsp;syntax (limited support)</div><img src="docs/syntax-dune.png"/>
</div>
<span style="float:left;margin-right:10px"><div>Structured view</div><img src="docs/structure.png"/></span>
<div style="float:left;margin-right:10px"><div>Code folding</div><img src="docs/folding.png"/></div>
<div style="float:left;margin-right:10px"><div>js language injection</div><img src="docs/lang-inject.png"/></div>
<div style="float:left;margin-right:10px"><div>Bucklescript compiler integration</div><img src="docs/bsb.png"/></div>
<div style="float:left;margin-right:10px"><div>JSX syntax & completion</div><img src="docs/jsx.png"/></div>
<div style="float:left;margin-right:10px"><div>Type annotation</div><img src="docs/type.png"/></div>
<div style="float:left;margin-right:10px"><div>Intentions (_ReasonML_): add braces to function, transform local open</div><img src="docs/intention.png"/></div>


Other features:
- Line and block commenting 
- Pair braces matcher
- Reformat using `refmt` (_press `crtl alt shift R` or `⌘⎇⇧R` on Mac_)
- Reformat on save
- Works natively on Windows

> Make sure `"namespace": false` is set in `bsconfig.json` to enable inferred types hints in the editor.

## How to help

- Be patient
- Give this project some love, star it or star the plugin page in [intellij repository](https://plugins.jetbrains.com/plugin/9440-reasonml-language-plugin)
- Fill the github repo with specific issues
- <a href="https://liberapay.com/hgiraud/donate"><img alt="Donate using Liberapay" src="https://liberapay.com/assets/widgets/donate.svg"></a> Support my work with liberapay
- <a href="https://www.paypal.me/rvgiraud"><img alt="Donate using paypal" src="https://img.shields.io/badge/paypal-me-blue.svg"></a> Support my work with paypal-me

To all the people who have donated, you are awesome !! Really, this is pure anonymous donation and it blows my mind... I'm very grateful and it's kinda stupid but it keeps me motivated. So big thanks.

Many thanks also to the [jetbrains team](https://www.jetbrains.com/?from=reasonml-idea-plugin) who provide me an OSS licence for their product.

## Some live templates

List of templates that may help development.

abbrevation: `jsp`, description: `create a jsProps in wrapReasonForJs`
```
~$NAME$=jsProps##$NAME$,$END$
```

abbreviation: `style`, description: `create a style module`
```
module Styles = {
    open Css;
    let $VAR$ = style([
        $END$
    ]);
};
```

abbreviation: `comp`, description: `create a reason component`
```
let component = ReasonReact.statelessComponent("$NAME$");

let make = (~$PROP$, _children) => {...component, render: _self => <div />};
$END$
/*
 JS interop
 expose an helper for js - can be deleted when no more used by javascript code
 */
let jsComponent =
  ReasonReact.wrapReasonForJs(~component, jsProps =>
    make(~$PROP$=jsProps##$PROP$, [||])
  );
```

## What?

- Why don't you consider using Language Server Protocol ?

  Mainly because I want a strong integration in the IDE and I need a first class support of Windows.

- What versions does the plugin support at the moment?

  It should work with idea 15

- How is it different than [ocaml language plugin](https://github.com/sidharthkuruvila/ocaml-ide)?

  I wanted to have a plugin dedicated to reason and that's why I created one called reasonml, not ocaml.
  Then I realised that I still need ocaml support and that people are asking for it.
    
  I started this plugin by studying and copying parts of the ocaml plugin, but now the 2 projects are taking different orientations, technically.
    
  I am moving away from merlin and trying to use the most of Intellij structures.
    
  Now, they have incompatible implementations, and different goals. 

- Modules from `node_modules` folder are not resolved (GoTo is not working on `Array.sort`)?

  Ensure that `node_modules` folder is not excluded from IDE index.

## Development

This project is heavily inspired by :
- [custom language tutorial](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html) from intellij
- [ocaml language plugin](https://github.com/sidharthkuruvila/ocaml-ide) from sidharthkuruvila ( :+1: )
