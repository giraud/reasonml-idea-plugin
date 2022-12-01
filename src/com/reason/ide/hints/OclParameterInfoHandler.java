package com.reason.ide.hints;

import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class OclParameterInfoHandler extends ORParameterInfoHandler {
    @Override public boolean isWhitespaceSensitive() {
        return true;
    }

    @Override
    int computeParameterIndex(@NotNull RPsiParameters paramsOwner, @NotNull UpdateParameterInfoContext context) {
        return ParameterInfoUtils.getCurrentParameterIndex(paramsOwner.getNode(), context.getOffset(), TokenType.WHITE_SPACE);
    }

    @Override
    @Nullable RPsiParameters findFunctionParams(@NotNull PsiFile file, int offset) {
        PsiElement elementAt = file.findElementAt(offset);

        if (elementAt == null) {
            // Maybe at the end of file
            if (offset == file.getTextLength()) {
                elementAt = file.findElementAt(offset - 1);
            }
        }

        if (elementAt instanceof PsiWhiteSpace) {
            elementAt = PsiTreeUtil.prevVisibleLeaf(elementAt);
            if (elementAt != null && elementAt.getNode().getElementType() == OclTypes.INSTANCE.LIDENT) {
                elementAt = elementAt.getParent();
            }
        }

        if (elementAt instanceof RPsiFunctionCall) {
            return ORUtil.findImmediateFirstChildOfClass(elementAt, RPsiParameters.class);
        }

        return elementAt == null ? null : PsiTreeUtil.getParentOfType(elementAt, RPsiParameters.class);
    }
}
