package com.reason.lang;

enum ParserScopeEnum {
    file,

    open,
    openModulePath,

    module,
    moduleNamed,
    moduleNamedEq,
    moduleBinding,

    let,
    letNamed,
    letNamedEq,
    letNamedEqParameters,
    letParameters,
    letFunBody,
    letBody,
    funBody,

    startTag,
    closeTag,
    tagProperty,
    tagPropertyEq,

    type,
    typeNamed,
    typeNamedEq,
    typeNamedEqPatternMatch,

    annotation,
    annotationName,

    objectBinding,

    paren,
    brace,
    bracket,
}
