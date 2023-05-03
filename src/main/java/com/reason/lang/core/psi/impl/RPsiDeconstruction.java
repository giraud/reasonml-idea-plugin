package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiDeconstruction extends ORCompositePsiElement<ORLangTypes> {

    protected RPsiDeconstruction(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @NotNull List<PsiElement> getDeconstructedElements() {
        List<PsiElement> result = new ArrayList<>();

        for (PsiElement child : getChildren()) {
            if (child instanceof RPsiDeconstruction) {
                // nested deconstructions
                result.addAll(((RPsiDeconstruction) child).getDeconstructedElements());
            } else {
                IElementType elementType = child.getNode().getElementType();
                if (elementType != myTypes.LPAREN && elementType != myTypes.RPAREN
                        && elementType != myTypes.LBRACE && elementType != myTypes.RBRACE
                        && elementType != myTypes.COMMA && elementType != myTypes.SEMI
                        && elementType != myTypes.UNDERSCORE
                        && !(child instanceof PsiWhiteSpace)) {
                    result.add(child);
                }
            }
        }

        return result;
    }
}
