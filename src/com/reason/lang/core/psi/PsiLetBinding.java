package com.reason.lang.core.psi;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiLetBinding extends ORCompositeTypePsiElement<ORTypes> {
    public PsiLetBinding(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
