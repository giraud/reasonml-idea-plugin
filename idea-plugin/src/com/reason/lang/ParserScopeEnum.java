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
  moduleBinding,

  let,
  letNamed,
  letNamedAttribute,
  letNamedEq,
  letNamedSignature,
  deconstruction,

  functionCallParams,

  functionMatch,

  val,
  valNamed,

  annotation,
  annotationName,
  annotationParameter,
  macro,
  macroName,
  macroRawNamed,
  macroRawBody,

  maybeRecord,
  recordBinding,
  recordField,

  bracket,

  jsObject,
  object,
  objectField,
  objectFieldNamed,

  match,
  matchWith,
  switch_,
  switchBody,

  try_,

  patternMatch,
  patternMatchVariant,
  patternMatchVariantConstructor,
  patternMatchBody,

  genericExpression,

  field,

  maybeFunctionParameters,

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

  tryBody,
  tryBodyWith,
  tryBodyWithHandler,

  signatureScope,
  signatureItem,

  moduleNamedWithType,
  scope,
  record,
  maybeRecordUsage,
  recordUsage,
  funPattern,
  fieldNamed,
  jsObjectBinding,
  moduleType,
  patternMatchValue,
}
