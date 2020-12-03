---
id: live-templates
title: Some Live Templates
sidebar_label: Live Templates
slug: /get-started/live-templates
---

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

