package com.reason.ide.hints;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

public class ResParameterInfoHandler extends ORParameterInfoHandler {
    public ResParameterInfoHandler() {
        super(ResTypes.INSTANCE);
    }

    @Override
    @Nullable ArgumentsDescription[] calculateParameterInfo(PsiFunctionCallParams paramsOwner) {
        PsiLowerSymbol functionName = PsiTreeUtil.getPrevSiblingOfType(paramsOwner, PsiLowerSymbol.class);
        PsiReference reference = functionName == null ? null : functionName.getReference();
        if (reference instanceof PsiLowerSymbolReference) {
            PsiElement resolvedRef = ((PsiLowerSymbolReference) reference).resolveInterface();
            PsiElement resolvedElement = (resolvedRef instanceof PsiLowerIdentifier || resolvedRef instanceof PsiUpperIdentifier)
                    ? resolvedRef.getParent() : resolvedRef;

            if (resolvedElement instanceof PsiQualifiedNamedElement) {
                LOG.trace("Resolved element", resolvedElement);
                if (resolvedElement instanceof PsiSignatureElement) {
                    PsiSignature signature = ((PsiSignatureElement) resolvedElement).getSignature();
                    if (signature != null) {
                        return new ArgumentsDescription[]{new ArgumentsDescription((PsiQualifiedNamedElement) resolvedElement, signature)};
                    }
                }
                // else {
                //   Try to read signature from cmt file - TODO #334
                // }
            }
        }

        return null;
    }

    @Override
    @Nullable PsiFunctionCallParams findFunctionParams(@NotNull PsiFile file, int offset) {
        PsiElement elementAt = file.findElementAt(offset);
        return elementAt == null ? null : PsiTreeUtil.getParentOfType(elementAt, PsiFunctionCallParams.class);
    }
}
