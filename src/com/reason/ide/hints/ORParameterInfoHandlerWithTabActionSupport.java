package com.reason.ide.hints;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORSignature;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ORParameterInfoHandlerWithTabActionSupport implements ParameterInfoHandlerWithTabActionSupport<PsiFunctionCallParams, ORSignature, PsiElement> {

    @NotNull
    @Override
    public IElementType getActualParameterDelimiterType() {
        return RmlTypes.INSTANCE.COMMA;
    }

    @NotNull
    @Override
    public IElementType getActualParametersRBraceType() {
        return RmlTypes.INSTANCE.RBRACE;
    }

    @NotNull
    @Override
    public Set<Class> getArgumentListAllowedParentClasses() {
        return Collections.singleton(PsiFunctionCallParams.class);
    }

    @NotNull
    @Override
    public Set<? extends Class> getArgListStopSearchClasses() {
        return java.util.Collections.emptySet();
    }

    @NotNull
    @Override
    public Class<PsiFunctionCallParams> getArgumentListClass() {
        return PsiFunctionCallParams.class;
    }

    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return null;
    }

    @NotNull
    @Override
    public PsiElement[] getActualParameters(@NotNull PsiFunctionCallParams paramsOwner) {
        Collection<PsiParameter> childrenOfType = paramsOwner.getParameterList();
        return childrenOfType.toArray(new PsiParameter[0]);
    }

    @Nullable
    @Override
    public PsiFunctionCallParams findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        return findFunctionParams(context.getFile(), context.getOffset());
    }

    @Nullable
    @Override
    public PsiFunctionCallParams findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
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
        PsiLowerSymbol functionName = PsiTreeUtil.getPrevSiblingOfType(paramsOwner, PsiLowerSymbol.class);
        if (functionName != null) {
            PsiReference reference = functionName.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolve();
            if (resolvedElement instanceof PsiNamedElement) {
                PsiElement resolvedParent = resolvedElement.getParent();
                if (resolvedParent instanceof PsiSignatureElement) {
                    PsiSignature signature = ((PsiSignatureElement) resolvedParent).getSignature();
                    if (signature != null) {
                        context.setItemsToShow(new Object[]{signature.asHMSignature()});
                        context.showHint(paramsOwner, paramsOwner.getTextOffset(), this);
                    } else if (resolvedParent instanceof PsiLet) {
                        PsiLet resolvedLet = (PsiLet) resolvedParent;
                        if (resolvedLet.isFunction()) {
                            // We don't have the real signature, we just display the function arguments
                            Collection<PsiParameter> parameters = resolvedLet.getFunction().getParameterList();
                            ORSignature hmSignature = new ORSignature(parameters);
                            context.setItemsToShow(new Object[]{hmSignature});
                            context.showHint(paramsOwner, paramsOwner.getTextOffset(), this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateParameterInfo(@NotNull PsiFunctionCallParams paramsOwner, @NotNull UpdateParameterInfoContext context) {
        if (context.getParameterOwner() == null || paramsOwner.equals(context.getParameterOwner())) {
            context.setParameterOwner(paramsOwner);
            context.setCurrentParameter(ParameterInfoUtils.getCurrentParameterIndex(paramsOwner.getNode(), context.getOffset(), getActualParameterDelimiterType()));
        } else {
            context.removeHint();
        }
    }

    @Override
    public void updateUI(@Nullable ORSignature signature, @NotNull ParameterInfoUIContext context) {
        if (signature == null) {
            context.setUIComponentEnabled(false);
            return;
        }

        int currentParameterIndex = context.getCurrentParameterIndex();
        ORSignature.SignatureType[] types = signature.getTypes();

        boolean grayedOut = types.length <= currentParameterIndex;
        context.setUIComponentEnabled(!grayedOut);

        TextRange paramRange = TextRange.EMPTY_RANGE;

        context.setupUIComponentPresentation(signature.toString(),
                paramRange.getStartOffset(),
                paramRange.getEndOffset(),
                !context.isUIComponentEnabled(),
                false,
                true,
                context.getDefaultParameterColor());
    }

    @Nullable
    private PsiFunctionCallParams findFunctionParams(@NotNull PsiFile file, int offset) {
        PsiElement elementAt = file.findElementAt(offset);
        if (elementAt != null) {
            return PsiTreeUtil.getParentOfType(elementAt, PsiFunctionCallParams.class);
        }
        return null;
    }


    //region Backward compatibility
    @Nullable
    @Override
    public Object[] getParametersForDocumentation(ORSignature hmSignature, ParameterInfoContext parameterInfoContext) {
        return new Object[0];
    }

    @Nullable
    @Override
    public String getParameterCloseChars() {
        return null;
    }

    @Override
    public boolean tracksParameterIndex() {
        return false;
    }
    //endregion
}
