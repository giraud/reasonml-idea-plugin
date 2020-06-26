package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

public class PsiDuneFields extends PsiToken<DuneTypes> {
    public PsiDuneFields(@NotNull DuneTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public String toString() {
        return "Fields";
    }
}
