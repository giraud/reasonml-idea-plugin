package com.reason.lang.dune;

import com.reason.lang.core.type.ORCompositeElementType;
import com.reason.lang.core.type.ORTokenElementType;

public class DuneTypes {

  public static final DuneTypes INSTANCE = new DuneTypes();

  private DuneTypes() {}

  // Composite element types

  public final ORCompositeElementType C_FIELD =
      new ORCompositeElementType("Field", DuneLanguage.INSTANCE);
  public final ORCompositeElementType C_FIELDS =
      new ORCompositeElementType("Fields", DuneLanguage.INSTANCE);
  public final ORCompositeElementType C_STANZA =
      new ORCompositeElementType("Stanza", DuneLanguage.INSTANCE);
  public final ORCompositeElementType C_SEXPR =
      new ORCompositeElementType("S-expr", DuneLanguage.INSTANCE);
  public final ORCompositeElementType C_VAR =
      new ORCompositeElementType("Var", DuneLanguage.INSTANCE);

  // Token element types

  public final ORTokenElementType ATOM = new ORTokenElementType("ATOM", DuneLanguage.INSTANCE);
  public final ORTokenElementType COLON = new ORTokenElementType("COLON", DuneLanguage.INSTANCE);
  public final ORTokenElementType COMMENT =
      new ORTokenElementType("COMMENT", DuneLanguage.INSTANCE);
  public final ORTokenElementType EQUAL = new ORTokenElementType("EQUAL", DuneLanguage.INSTANCE);
  public final ORTokenElementType LPAREN = new ORTokenElementType("LPAREN", DuneLanguage.INSTANCE);
  public final ORTokenElementType RPAREN = new ORTokenElementType("RPAREN", DuneLanguage.INSTANCE);
  public final ORTokenElementType SHARP = new ORTokenElementType("SHARP", DuneLanguage.INSTANCE);
  public final ORTokenElementType STRING = new ORTokenElementType("String", DuneLanguage.INSTANCE);
  public final ORTokenElementType VAR_END =
      new ORTokenElementType("VAR_END", DuneLanguage.INSTANCE);
  public final ORTokenElementType VAR_START =
      new ORTokenElementType("VAR_START", DuneLanguage.INSTANCE);
}
