package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

public class RPsiDuneSExpr extends RPsiToken<DuneTypes> {
    public RPsiDuneSExpr(@NotNull DuneTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public @NotNull String toString() {
        return "S-Expression";
    }
}
