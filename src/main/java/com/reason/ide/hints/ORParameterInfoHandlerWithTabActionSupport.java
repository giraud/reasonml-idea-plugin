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

public class ORParameterInfoHandlerWithTabActionSupport implements ParameterInfoHandlerWithTabActionSupport<PsiFunctionCallParams, Object, PsiElement> {

    @NotNull
    @Override
    public PsiElement[] getActualParameters(@NotNull PsiFunctionCallParams callParams) {
        Collection<PsiFunctionParameter> childrenOfType = PsiTreeUtil.findChildrenOfType(callParams, PsiFunctionParameter.class);
        return childrenOfType.toArray(new PsiFunctionParameter[0]);
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
                PsiElement parent = resolvedElement.getParent();
                if (parent instanceof PsiSignatureElement) {
                    PsiSignature signature = ((PsiSignatureElement) parent).getSignature();
                    if (signature != null) {
                        context.setItemsToShow(new Object[]{signature});
                        context.showHint(params, params.getTextOffset(), this);
                    }
                }
            }
        }
    }

    @Override
    public void updateParameterInfo(@NotNull PsiFunctionCallParams psiFunctionCallParams, @NotNull UpdateParameterInfoContext context) {
        Collection<PsiFunctionParameter> parameters = psiFunctionCallParams.getParameterList();
        if (!parameters.isEmpty()) {
            int offset = context.getOffset();
            int i = 0;

            for (PsiFunctionParameter parameter : parameters) {
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
    public void updateUI(Object element, @NotNull ParameterInfoUIContext context) {
        if (element == null) {
            context.setUIComponentEnabled(false);
            return;
        }

        if (element instanceof PsiSignature) {
            PsiSignature st = (PsiSignature) element;
            HMSignature.SignatureType[] types = st.asHMSignature().getTypes();

            int currentParameterIndex = context.getCurrentParameterIndex();
            boolean grayedOut = currentParameterIndex != -1 && types.length <= currentParameterIndex;
            context.setUIComponentEnabled(!grayedOut);


            TextRange paramRange = getSignatureTextRange(st, currentParameterIndex);

            context.setupUIComponentPresentation(st.asHMSignature().toString(),
                    paramRange.getStartOffset(),
                    paramRange.getEndOffset(),
                    false, //!context.isUIComponentEnabled(),
                    false,
                    false,
                    context.getDefaultParameterColor());
        }
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
