package com.reason.lang;

public enum ParserScopeEnum {
  file,

  type,
  typeNamed,
  typeNamedParameters,
  typeNamedEq,
  typeNamedEqVariant,

  module,
  moduleNamedEq,
  moduleNamedColon,
  moduleNamedSignature,
  moduleNamedWithType,
  moduleBinding,
  moduleType,
  moduleTypeExtraction,

  let,
  letNamed,
  letNamedEq,
  letNamedSignature,
  deconstruction,

  maybeFunctionParameters,
  functionCallParams,
  functionMatch,

  macroRaw,

  maybeRecord,
  record,
  recordBinding,
  maybeRecordUsage,
  recordUsage,

  jsObject,
  jsObjectBinding,
  object,
  objectField,
  objectFieldNamed,
  field,
  fieldNamed,

  match,
  matchWith,
  switchBody,

  patternMatch,
  patternMatchVariant,
  patternMatchBody,
  patternMatchValue,
  funPattern,

  genericExpression,

  functorNamed,
  functorNamedEq,
  functorParams,
  functorParam,
  functorParamColon,
  functorParamColonSignature,
  functorNamedColon,
  functorNamedEqColon,
  functorResult,
  functorConstraints,
  functorConstraint,
  functorBinding,
  maybeFunctorCall,
  functorCall,

  signatureScope,
  scope,
}
