package com.reason.lang;

enum ParserScopeEnum {
    file,

    open,
    openModulePath,

    let,
    letNamed,
    letNamedEq,
    letNamedEqParameters,
    letParameters,
    letFunBody,

    startTag,
    closeTag,
}
