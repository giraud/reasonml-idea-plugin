package com.reason.ide.search;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.reason.lang.core.psi.*;
import com.reason.lang.ocaml.OclLexer;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OclFindUsagesProvider implements com.intellij.lang.findUsages.FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        OclTypes types = OclTypes.INSTANCE;
        return new DefaultWordsScanner(
                new OclLexer(),
                TokenSet.create((IElementType) types.C_UPPER_SYMBOL, (IElementType) types.C_LOWER_SYMBOL, (IElementType) types.C_VARIANT),
                TokenSet.EMPTY,
                TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof PsiUpperSymbol || element instanceof PsiLowerSymbol;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        String type = PsiTypeElementProvider.getType(element);
        return type == null ? "unknown type" : type;
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            return name == null ? "" : name;
        }

        return "desc name of element ";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            return name == null ? "" : name;
        }

        return "";
    }
}
