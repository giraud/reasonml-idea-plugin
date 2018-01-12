package com.reason.lang;

public enum ParserScopeEnum {
    file,

    open,
    openModulePath,

    include,

    external,
    externalNamed,

    type,
    typeNamed,
    typeNamedEq,
    typeNamedEqPatternMatch,

    module,
    moduleNamed,
    moduleNamedEq,
    moduleNamedColon,
    moduleNamedSignature,
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

    val,
    valNamed,

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

    _if,
    binaryCondition,
    ifThenStatement,
    match,
    matchBinaryCondition,
    matchWith,
    _try,
    tryBinaryCondition,
    tryWith,
}
