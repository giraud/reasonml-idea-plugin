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
    typeConstrNameParameters,
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

    object,
    objectField,
    objectFieldNamed,

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
    interpolationString,
    interpolationPart,
    interpolationReference,

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
    functorNamedEqParamsArrow,
    functorDeclarationParams,
    functorParams,
    functorParam,
    functorParamColon,
    functorParamColonSignature,
    functorNamedColon,
    functorNamedEqColon,
    functorConstraints,
    functorBinding,

    tryBody,
    tryBodyWith,
    tryBodyWithHandler,

    signature,
    signatureScope,
    signatureParams,
    signatureItem,
    signatureItemEq,

    whileLoop,
    whileConditionLoop,
    whileDoLoop,
    doLoop,

    valNamedSymbol, struct, matchException, beginScope, ifElseStatement, bracketGt, moduleDeclaration,
    moduleInstanciation, moduleNamedColonWith, moduleNamedWithType, letBinding, scope,
    moduleNamedSignatureEq, array,  objectScope, clazzDeclaration, clazz, clazzNamed, clazzNamedEq,
    clazzBodyScope, clazzConstructor, clazzField, clazzFieldNamed, clazzMethod, clazzMethodNamed, clazzNamedParameters,
    clazzNamedConstructor, record, mixin,
    externalNamedSignatureEq,
    patternMatchConstructor, maybeRecordUsage, recordUsage, letNamedBindingFunction,
    typeBinding, recordFieldAnnotation, option, optionParameter, patternMatchVariant,
    name, variant, variantConstructor, variantConstructorParameter, variantConstructorParameters, unit, localObjectOpen
}
