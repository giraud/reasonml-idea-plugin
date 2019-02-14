package com.reason.lang;

public enum ParserScopeEnum {
    file,

    open,
    include,

    localOpen,
    localOpenScope,

    external,
    externalNamed,
    externalNamedSignature,

    type,
    typeConstrName,
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

    modulePath,

    let,
    letNamed,
    letNamedEq,
    letNamedSignature,
    letNamedBinding,

    maybeFunctionParameter,

    function,
    functionParameters,
    functionParameter,
    functionParameterNamed,
    functionParameterNamedSignature,
    functionParameterNamedSignatureItem,
    functionBody,
    functionCall,
    functionCallParams,

    val,
    valNamed,
    valNamedSignature,

    jsxTag,
    jsxStartTag,
    jsxTagProperty,
    jsxTagPropertyEq,
    jsxTagPropertyEqValue,
    jsxTagBody,
    jsxTagClose,

    annotation,
    annotationName,
    macro,
    macroName,
    macroNamed,
    macroRawNamed,
    macroRaw,
    macroRawBody,
    raw,
    rawBody,

    maybeRecord,
    recordBinding,
    recordField,
    recordSignature,

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
    patternMatchBody,

    try_,
    tryScope,

    patternMatch,

    multilineStart,
    interpolationStart,
    interpolationString,

    genericExpression,

    assert_,
    sexpr,
    library,
    executable,

    maybeFunction,
    maybeFunctionParameters,

    functorDeclaration,
    functorNamed,
    functorNamedEq,
    functorDeclarationParams,
    functorParams,
    functorNamedEqColon,
    functorNamedColon,
    functorConstraints,
    functorBinding,

    valNamedSymbol, struct, matchException, beginScope, ifElseStatement, bracketGt, moduleDeclaration,
    moduleInstanciation, tryWith, moduleNamedColonWith, moduleNamedWithType, doLoop, tryWithScope, letBinding, scope,
    moduleNamedSignatureEq, array, signature, objectScope, clazzDeclaration, clazz, clazzNamed, clazzNamedEq,
    clazzBodyScope, clazzConstructor, clazzField, clazzFieldNamed, clazzMethod, clazzMethodNamed, clazzNamedParameters,
    clazzNamedConstructor, record, mixin,
    externalNamedSignatureEq, jsObjectField, jsObjectFieldNamed,
    patternMatchConstructor, maybeRecordUsage, recordUsage, signatureItem, letNamedBindingFunction,
    variantConstructor, variantConstructorParameter, variantConstructorParameters, typeBinding, signatureScope, recordFieldAnnotation, signatureParams, option, optionParameter, patternMatchVariant, variant, name
}
