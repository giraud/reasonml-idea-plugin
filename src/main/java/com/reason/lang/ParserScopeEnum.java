package com.reason.lang;

enum ParserScopeEnum {
    file,

    open,
    openModulePath,

    external,
    externalNamed,

    type,
    typeNamed,
    typeNamedEq,
    typeNamedEqPatternMatch,

    module,
    moduleNamed,
    moduleNamedEq,
    moduleSignature,
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

    annotation,
    annotationName,
    macro,
    macroName,
    
    objectBinding,

    paren,
    brace,
    bracket,

    exception,
    exceptionNamed,
}
