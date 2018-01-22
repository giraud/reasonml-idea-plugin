package com.reason.lang;

public enum ParserScopeEnum {
    file,

    open,
    openModulePath,

    include,

    external,
    externalNamed,
    externalNamedSignature,

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

    if_,
    binaryCondition,
    ifThenStatement,
    match,
    matchBinaryCondition,
    matchWith,
    switch_,
    switchBinaryCondition,
    try_,
    tryBinaryCondition,
    tryWith,
}
