package com.reason.lang.dune;

import com.intellij.lang.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

class DuneASTFactory extends ASTFactory {
    private final DuneTypes myTypes;

    public DuneASTFactory() {
        myTypes = DuneTypes.INSTANCE;
    }

    @Override
    public @Nullable CompositeElement createComposite(@NotNull IElementType type) {
        if (type == DuneTypes.INSTANCE.C_FIELD) {
            return new RPsiDuneField(myTypes, type);
        } else if (type == DuneTypes.INSTANCE.C_FIELDS) {
            return new RPsiDuneFields(myTypes, type);
        } else if (type == myTypes.C_STANZA) {
            return new RPsiDuneStanza(myTypes, type);
        } else if (type == DuneTypes.INSTANCE.C_VAR) {
            return new RPsiDuneVar(myTypes, type);
        } else if (type == DuneTypes.INSTANCE.C_SEXPR) {
            return new RPsiDuneSExpr(myTypes, type);
        }

        return null;
    }

    @Override
    public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
        return super.createLeaf(type, text);
    }
}
