package com.reason.ide.hints;

import com.intellij.codeInsight.lookup.*;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public abstract class ORParameterInfoHandler implements ParameterInfoHandler<PsiFunctionCallParams, RmlParameterInfoHandler.ArgumentsDescription> {
    protected static final Log LOG = Log.create("param");

    private final ORTypes myTypes;

    protected ORParameterInfoHandler(ORTypes types) {
        myTypes = types;
    }

    @Nullable abstract RmlParameterInfoHandler.ArgumentsDescription[] calculateParameterInfo(PsiFunctionCallParams paramsOwner);

    @Nullable abstract PsiFunctionCallParams findFunctionParams(@NotNull PsiFile file, int offset);

    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Override
    public @Nullable Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return null;
    }

    @Override
    public @Nullable PsiFunctionCallParams findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        PsiFunctionCallParams paramsOwner = findFunctionParams(context.getFile(), context.getOffset());
        context.setItemsToShow(calculateParameterInfo(paramsOwner));
        return paramsOwner;
    }

    @Override
    public @Nullable PsiFunctionCallParams findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        PsiFunctionCallParams paramsOwner = findFunctionParams(context.getFile(), context.getOffset());
        if (paramsOwner != null) {
            PsiElement currentOwner = context.getParameterOwner();
            if (currentOwner == null || currentOwner == paramsOwner) {
                return paramsOwner;
            }
        }

        return null;
    }

    @Override
    public void showParameterInfo(@NotNull PsiFunctionCallParams paramsOwner, @NotNull CreateParameterInfoContext context) {
        context.showHint(paramsOwner, paramsOwner.getTextOffset(), this);
    }

    @Override
    public void updateParameterInfo(@NotNull PsiFunctionCallParams paramsOwner, @NotNull UpdateParameterInfoContext context) {
        if (context.getParameterOwner() == null || paramsOwner.equals(context.getParameterOwner())) {
            int paramIndex = ParameterInfoUtils.getCurrentParameterIndex(paramsOwner.getNode(), context.getOffset(), myTypes.COMMA);
            context.setParameterOwner(paramsOwner);
            context.setCurrentParameter(paramIndex);
        } else {
            context.removeHint();
        }
    }

    @Override
    public void updateUI(@Nullable RmlParameterInfoHandler.ArgumentsDescription arguments, @NotNull ParameterInfoUIContext context) {
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

    static class ArgumentsDescription {
        PsiQualifiedNamedElement resolvedElement;
        PsiSignature signature;
        String[] arguments; // ?

        public ArgumentsDescription(PsiQualifiedNamedElement resolvedElement, PsiSignature signature) {
            this.resolvedElement = resolvedElement;
            this.signature = signature;
            this.arguments = signature.getItems().stream().map(PsiElement::getText).toArray(String[]::new);
        }

        String getText() {
            return signature.getText();
        }

        TextRange getRange(int index) {
            if (index < 0 || index >= arguments.length) {
                return TextRange.EMPTY_RANGE;
            }

            return signature.getItems().get(index).getTextRangeInParent();
        }
    }
}
