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

    startTag,
    closeTag,
    tagProperty,
    tagPropertyEq,

    type,
    typeNamed,
    typeNamedEq,

    annotation,
    annotationName,
}
