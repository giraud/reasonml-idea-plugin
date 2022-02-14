package com.reason.ide.hints;

import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public abstract class ORParameterInfoHandler implements ParameterInfoHandler<PsiParameters, ORParameterInfoHandler.ArgumentsDescription> {
    protected static final Log LOG = Log.create("param");

    @Nullable abstract PsiParameters findFunctionParams(@NotNull PsiFile file, int offset);

    abstract int computeParameterIndex(@NotNull PsiParameters paramsOwner, @NotNull UpdateParameterInfoContext context);

    @Override
    public @Nullable PsiParameters findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        PsiParameters paramsOwner = findFunctionParams(context.getFile(), context.getOffset());
        if (paramsOwner != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found parameters for function", paramsOwner.getParent().getText());
            }
            context.setItemsToShow(calculateParameterInfo(paramsOwner));
        }
        return paramsOwner;
    }

    @Override
    public @Nullable PsiParameters findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiParameters paramsOwner = findFunctionParams(context.getFile(), context.getOffset());
        if (paramsOwner != null) {
            PsiElement currentOwner = context.getParameterOwner();
            if (currentOwner == null || currentOwner == paramsOwner) {
                return paramsOwner;
            }
        }

        return null;
    }

    @Override
    public void showParameterInfo(@NotNull PsiParameters paramsOwner, @NotNull CreateParameterInfoContext context) {
        context.showHint(paramsOwner, paramsOwner.getTextOffset(), this);
    }

    @Override
    public void updateParameterInfo(@NotNull PsiParameters paramsOwner, @NotNull UpdateParameterInfoContext context) {
        if (context.getParameterOwner() == null || paramsOwner.equals(context.getParameterOwner())) {
            int paramIndex = computeParameterIndex(paramsOwner, context);
            context.setParameterOwner(paramsOwner);
            context.setCurrentParameter(paramIndex);
        } else {
            context.removeHint();
        }
    }

    @Override
    public void updateUI(@Nullable ORParameterInfoHandler.ArgumentsDescription arguments, @NotNull ParameterInfoUIContext context) {
        if (arguments == null) {
            context.setUIComponentEnabled(false);
            return;
        }

        int paramIndex = context.getCurrentParameterIndex();
        TextRange paramRange = arguments.getRange(paramIndex);

        context.setupUIComponentPresentation(
                arguments.getText(),
                paramRange.getStartOffset(),
                paramRange.getEndOffset(),
                !context.isUIComponentEnabled(),
                false,
                true,
                context.getDefaultParameterColor());
    }

    ArgumentsDescription @Nullable [] calculateParameterInfo(@NotNull PsiParameters paramsOwner) {
        PsiElement resolvedElement = null;

        PsiElement parent = paramsOwner.getParent();
        if (parent instanceof PsiFunctionCall) {
            PsiElement functionName = parent.getFirstChild();
            PsiReference reference = functionName == null ? null : functionName.getReference();
            if (reference instanceof PsiLowerSymbolReference) {
                PsiElement resolvedRef = ((PsiLowerSymbolReference) reference).resolveInterface();
                resolvedElement = (resolvedRef instanceof PsiLowerIdentifier) ? resolvedRef.getParent() : resolvedRef;
            }
        } else if (parent instanceof PsiFunctorCall) {
            PsiElement functorName = parent.getFirstChild();
            // todo
        }

        if (resolvedElement instanceof PsiQualifiedNamedElement) {
            LOG.trace("Resolved element", resolvedElement);
            if (resolvedElement instanceof PsiSignatureElement) {
                PsiSignature signature = ((PsiSignatureElement) resolvedElement).getSignature();
                if (signature != null) {
                    return new ArgumentsDescription[]{new ArgumentsDescription((PsiQualifiedNamedElement) resolvedElement, signature)};
                }
            }
        }

        return null;
    }

    static class ArgumentsDescription {
        final PsiQualifiedNamedElement resolvedElement;
        final PsiSignature signature;
        final String[] arguments; // ?

        public ArgumentsDescription(PsiQualifiedNamedElement resolvedElement, @NotNull PsiSignature signature) {
            this.resolvedElement = resolvedElement;
            this.signature = signature;
            this.arguments = signature.getItems().stream().map(PsiElement::getText).toArray(String[]::new);
        }

        String getText() {
            return signature.getText();
        }

        @NotNull TextRange getRange(int index) {
            if (index < 0 || index >= arguments.length) {
                return TextRange.EMPTY_RANGE;
            }

            return signature.getItems().get(index).getTextRangeInParent();
        }
    }
}
