package com.reason.lang;

public enum ParserScopeEnum {
    file,

    open,

    include,

    external,
    externalNamed,
    externalNamedSignature,

    type,
    typeNamed,
    typeNamedEq,
    typeNamedEqVariant,

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
    letNamedParametersEq,
    letNamedSignature,
    letParameters,
    letFunBody,

    val,
    valNamed,
    valNamedSignature,

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

    jsObject,

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
    switchBody,
    switchPattern,
    patternMatchBody,
    try_,
    tryBinaryCondition,

    patternMatch,

    multilineStart,
    interpolationStart,
    interpolationString,

    namedSymbol,
    namedSymbolSignature,

    genericExpression
}
