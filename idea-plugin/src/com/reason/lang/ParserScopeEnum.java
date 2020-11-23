package com.reason.lang;

public enum ParserScopeEnum {
  module,
  moduleNamedColon,
  moduleNamedSignature,
  moduleBinding,
  moduleTypeExtraction,

  letNamed,

  maybeFunctionParameters,
  functionMatch,

  macroRaw,

  maybeRecord,
  recordBinding,
  maybeRecordUsage,
  recordUsage,

  jsObject,
  jsObjectBinding,
  objectFieldNamed,
  field,
  fieldNamed,

  matchWith,
  switchBody,

  patternMatchVariant,
  patternMatchValue,
  funPattern,

  genericExpression,

  functorNamedEq,
  functorNamedColon,
  functorNamedEqColon,
  maybeFunctorCall,

  signatureScope,
  scope,
}
