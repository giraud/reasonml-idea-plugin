package com.reason.lang;

enum ParserScopeEnum {
    file,

    open,
    openModulePath,

    module,
    moduleNamed,

    let,
    letNamed,
    letNamedEq,
    letNamedEqParameters,
    letParameters,
    letFunBody,

    startTag,
    closeTag,
    tagProperty,
    tagPropertyEq,
}
