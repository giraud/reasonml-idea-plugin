package com.reason.ide.hints;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ORParameterInfoHandlerWithTabActionSupport implements ParameterInfoHandlerWithTabActionSupport<PsiFunctionCallParams, HMSignature, PsiElement> {

    @NotNull
    @Override
    public PsiElement[] getActualParameters(@NotNull PsiFunctionCallParams callParams) {
        Collection<PsiParameter> childrenOfType = PsiTreeUtil.findChildrenOfType(callParams, PsiParameter.class);
        return childrenOfType.toArray(new PsiParameter[0]);
    }

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
        return false;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return null;
    }

    @Nullable
    @Override
    public PsiFunctionCallParams findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        return getPsiFunctionCallParams(context);
    }

    @Nullable
    @Override
    public PsiFunctionCallParams findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        return getPsiFunctionCallParams(context);
    }

    private PsiFunctionCallParams getPsiFunctionCallParams(ParameterInfoContext context) {
        PsiElement elementAt = context.getFile().findElementAt(context.getOffset());
        return PsiTreeUtil.getParentOfType(elementAt, PsiFunctionCallParams.class);
    }

    @Override
    public void showParameterInfo(@NotNull PsiFunctionCallParams params, @NotNull CreateParameterInfoContext context) {
        PsiLowerSymbol functionName = PsiTreeUtil.getPrevSiblingOfType(params, PsiLowerSymbol.class);
        if (functionName != null) {
            PsiReference reference = functionName.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolve();
            if (resolvedElement instanceof PsiLowerSymbol) {
                PsiElement resolvedParent = resolvedElement.getParent();
                if (resolvedParent instanceof PsiSignatureElement) {
                    PsiSignature signature = ((PsiSignatureElement) resolvedParent).getSignature();
                    if (signature != null) {
                        context.setItemsToShow(new Object[]{signature.asHMSignature()});
                        context.showHint(params, params.getTextOffset(), this);
                    } else if (resolvedParent instanceof PsiLet) {
                        PsiLet resolvedLet = (PsiLet) resolvedParent;
                        if (resolvedLet.isFunction()) {
                            // We don't have the real signature, we just display the function arguments
                            Collection<PsiParameter> parameters = resolvedLet.getFunction().getParameterList();
                            HMSignature hmSignature = new HMSignature(parameters);
                            context.setItemsToShow(new Object[]{hmSignature});
                            context.showHint(params, params.getTextOffset(), this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateParameterInfo(@NotNull PsiFunctionCallParams psiFunctionCallParams, @NotNull UpdateParameterInfoContext context) {
        Collection<PsiParameter> parameters = psiFunctionCallParams.getParameterList();
        if (!parameters.isEmpty()) {
            int offset = context.getOffset();
            int i = 0;

            for (PsiParameter parameter : parameters) {
                TextRange textRange = parameter.getTextRange();
                if (textRange.getStartOffset() <= offset && offset <= textRange.getEndOffset()) {
                    context.setCurrentParameter(i);
                    break;
                }
                i++;
            }
        }
    }

    @Override
    public void updateUI(HMSignature element, @NotNull ParameterInfoUIContext context) {
        if (element == null) {
            context.setUIComponentEnabled(false);
            return;
        }

        HMSignature.SignatureType[] types = element.getTypes();

        int currentParameterIndex = context.getCurrentParameterIndex();
        boolean grayedOut = currentParameterIndex != -1 && types.length <= currentParameterIndex;
        context.setUIComponentEnabled(!grayedOut);

        TextRange paramRange = TextRange.EMPTY_RANGE; //getSignatureTextRange(element, currentParameterIndex);

        context.setupUIComponentPresentation(element.toString(),
                paramRange.getStartOffset(),
                paramRange.getEndOffset(),
                false, //!context.isUIComponentEnabled(),
                false,
                false,
                context.getDefaultParameterColor());
    }

    private TextRange getSignatureTextRange(PsiSignature st, int index) {
        Collection<PsiSignatureItem> items = PsiTreeUtil.findChildrenOfType(st, PsiSignatureItem.class);
        if (index == -1 || items.size() <= index) {
            return TextRange.EMPTY_RANGE;
        }

        int i = 0;
        for (PsiSignatureItem item : items) {
            if (i == index) {
                TextRange textRange = TextRange.create(item.getStartOffsetInParent(), item.getStartOffsetInParent() + item.getTextLength());
                return textRange;
            }
            i++;
        }

        return TextRange.EMPTY_RANGE;
    }
}
