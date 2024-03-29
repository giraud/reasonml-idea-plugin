package com.reason.lang.dune;

import com.reason.lang.core.type.*;

public class DuneTypes extends ORTypes {
    public static final DuneTypes INSTANCE = new DuneTypes();

    private DuneTypes() {
    }

    public final ORTokenElementType SINGLE_COMMENT = new ORTokenElementType("SINGLE_COMMENT", DuneLanguage.INSTANCE);
    public final ORTokenElementType MULTI_COMMENT = new ORTokenElementType("MULTI_COMMENT", DuneLanguage.INSTANCE);

    // Composite element types

    public final ORCompositeElementType C_FIELD = new ORCompositeElementType("Field", DuneLanguage.INSTANCE);
    public final ORCompositeElementType C_FIELDS = new ORCompositeElementType("Fields", DuneLanguage.INSTANCE);
    public final ORCompositeElementType C_STANZA = new ORCompositeElementType("Stanza", DuneLanguage.INSTANCE);
    public final ORCompositeElementType C_SEXPR = new ORCompositeElementType("S-expr", DuneLanguage.INSTANCE);
    public final ORCompositeElementType C_VAR = new ORCompositeElementType("Var", DuneLanguage.INSTANCE);

    // Token element types

    public final ORTokenElementType ATOM = new ORTokenElementType("ATOM", DuneLanguage.INSTANCE);
    public final ORTokenElementType COLON = new ORTokenElementType("COLON", DuneLanguage.INSTANCE);
    public final ORTokenElementType EQUAL = new ORTokenElementType("EQUAL", DuneLanguage.INSTANCE);
    public final ORTokenElementType GT = new ORTokenElementType("GT", DuneLanguage.INSTANCE);
    public final ORTokenElementType GTE = new ORTokenElementType("GTE", DuneLanguage.INSTANCE);
    public final ORTokenElementType LPAREN = new ORTokenElementType("LPAREN", DuneLanguage.INSTANCE);
    public final ORTokenElementType LT = new ORTokenElementType("LT", DuneLanguage.INSTANCE);
    public final ORTokenElementType LTE = new ORTokenElementType("LTE", DuneLanguage.INSTANCE);
    public final ORTokenElementType RPAREN = new ORTokenElementType("RPAREN", DuneLanguage.INSTANCE);
    public final ORTokenElementType SHARP = new ORTokenElementType("SHARP", DuneLanguage.INSTANCE);
    public final ORTokenElementType STRING = new ORTokenElementType("String", DuneLanguage.INSTANCE);
    public final ORTokenElementType VAR_END = new ORTokenElementType("VAR_END", DuneLanguage.INSTANCE);
    public final ORTokenElementType VAR_START = new ORTokenElementType("VAR_START", DuneLanguage.INSTANCE);
}
