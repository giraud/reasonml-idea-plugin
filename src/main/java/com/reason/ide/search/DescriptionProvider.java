package com.reason.ide.search;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import com.reason.lang.core.psi.PsiModuleName;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiTypeName;
import com.reason.lang.core.psi.PsiVarName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DescriptionProvider implements ElementDescriptionProvider {


    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        if (location == UsageViewNodeTextLocation.INSTANCE && element instanceof PsiNamedElement) {
            return getElementDescription(element, UsageViewShortNameLocation.INSTANCE);
        }

        if (location == UsageViewShortNameLocation.INSTANCE || location == UsageViewLongNameLocation.INSTANCE) {
            if (element instanceof PsiNamedElement) {
                return ((PsiNamedElement) element).getName();
            }
        }

        if (location == UsageViewTypeLocation.INSTANCE) {
            if (element instanceof PsiModuleName) {
                return "module";
            } else if (element instanceof PsiTypeName) {
                return "type";
            } else if (element instanceof PsiVarName) {
                return "let";
            }
        }

        return null;
    }
}
