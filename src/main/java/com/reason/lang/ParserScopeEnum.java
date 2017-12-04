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

    any,
}
