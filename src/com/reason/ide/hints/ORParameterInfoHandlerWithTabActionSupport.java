package com.reason.ide.hints;

import com.intellij.codeInsight.lookup.*;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.ide.search.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORParameterInfoHandlerWithTabActionSupport implements ParameterInfoHandlerWithTabActionSupport<PsiFunctionCallParams, PsiSignature, PsiElement> {
    @Override
    public @NotNull IElementType getActualParameterDelimiterType() {
        return RmlTypes.INSTANCE.COMMA;
    }

    @Override
    public @NotNull IElementType getActualParametersRBraceType() {
        return RmlTypes.INSTANCE.RBRACE;
    }

    @Override
    public @NotNull Set<Class<?>> getArgumentListAllowedParentClasses() {
        return Collections.singleton(PsiFunctionCallParams.class);
    }

    @Override
    public @NotNull Set<? extends Class<?>> getArgListStopSearchClasses() {
        return java.util.Collections.emptySet();
    }

    @Override
    public @NotNull Class<PsiFunctionCallParams> getArgumentListClass() {
        return PsiFunctionCallParams.class;
    }

    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Override
    public @Nullable Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return null;
    }

    @Override
    public @NotNull PsiElement[] getActualParameters(@NotNull PsiFunctionCallParams paramsOwner) {
        Collection<PsiParameter> childrenOfType = paramsOwner.getParametersList();
        return childrenOfType.toArray(new PsiElement[0]);
    }

    @Override
    public @Nullable PsiFunctionCallParams findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        return findFunctionParams(context.getFile(), context.getOffset());
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
        PsiLowerSymbol functionName = PsiTreeUtil.getPrevSiblingOfType(paramsOwner, PsiLowerSymbol.class);
        if (functionName != null) {
            PsiReference reference = functionName.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolve();
            if (resolvedElement instanceof PsiNamedElement) {
                PsiElement resolvedParent = resolvedElement.getParent();

                if (resolvedParent instanceof PsiLet) {
                    // If it's an alias, resolve to the alias
                    String alias = ((PsiLet) resolvedParent).getAlias();
                    if (alias != null) {
                        Project project = resolvedElement.getProject();
                        PsiFinder psiFinder = PsiFinder.getInstance(project);
                        PsiVal valFromAlias = psiFinder.findValFromQn(alias);
                        if (valFromAlias == null) {
                            PsiLet letFromAlias = psiFinder.findLetFromQn(alias);
                            if (letFromAlias != null) {
                                resolvedParent = letFromAlias;
                            }
                        } else {
                            resolvedParent = valFromAlias;
                        }
                    }
                }

                if (resolvedParent instanceof PsiSignatureElement) {
                    PsiSignature signature = ((PsiSignatureElement) resolvedParent).getSignature();
                    if (signature != null) {
                        context.setItemsToShow(new Object[]{signature});
                        context.showHint(paramsOwner, paramsOwner.getTextOffset(), this);
                    } else if (resolvedParent instanceof PsiLet) {
                        PsiLet resolvedLet = (PsiLet) resolvedParent;
                        if (resolvedLet.isFunction()) {
                            // We don't have the real signature, we just display the function arguments
                            PsiFunction function = resolvedLet.getFunction();
                            if (function != null) {
                                Collection<PsiParameter> parameters = function.getParameters();
                                context.setItemsToShow(new Object[]{});
                                context.showHint(paramsOwner, paramsOwner.getTextOffset(), this);
                            }
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
            context.setCurrentParameter(
                    ParameterInfoUtils.getCurrentParameterIndex(
                            paramsOwner.getNode(), context.getOffset(), getActualParameterDelimiterType()));
        } else {
            context.removeHint();
        }
    }

    @Override
    public void updateUI(@Nullable PsiSignature signature, @NotNull ParameterInfoUIContext context) {
        if (signature == null) {
            context.setUIComponentEnabled(false);
            //return;
        }

    /* TODO:
    int currentParameterIndex = context.getCurrentParameterIndex();
    ORSignature.SignatureType[] types = signature.getTypes();

    boolean grayedOut = types.length <= currentParameterIndex;
    context.setUIComponentEnabled(!grayedOut);

    TextRange paramRange = TextRange.EMPTY_RANGE;

    context.setupUIComponentPresentation(
        signature.asParameterInfo(OclLanguage.INSTANCE),
        paramRange.getStartOffset(),
        paramRange.getEndOffset(),
        !context.isUIComponentEnabled(),
        false,
        true,
        context.getDefaultParameterColor());
     */
    }

    @Nullable
    private PsiFunctionCallParams findFunctionParams(@NotNull PsiFile file, int offset) {
        PsiElement elementAt = file.findElementAt(offset);
        if (elementAt != null) {
            return PsiTreeUtil.getParentOfType(elementAt, PsiFunctionCallParams.class);
        }
        return null;
    }
}
