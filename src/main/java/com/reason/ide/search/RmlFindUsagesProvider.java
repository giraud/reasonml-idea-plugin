package com.reason.ide.search;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.tree.TokenSet;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.RmlLexer;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlFindUsagesProvider implements com.intellij.lang.findUsages.FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new RmlLexer(),
                TokenSet.create(RmlTypes.INSTANCE.UPPER_SYMBOL, RmlTypes.INSTANCE.LOWER_SYMBOL), TokenSet.EMPTY,
                TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof PsiLet || element instanceof PsiUpperSymbol || element instanceof PsiLowerSymbol;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof PsiUpperSymbol || element instanceof PsiModule) {
            return "module";
        }
        if (element instanceof PsiLowerSymbol) {
            return "symbol";
        }
        if (element instanceof PsiLet) {
            return "let";
        }
        if (element instanceof PsiExternal) {
            return "external";
        }
        if (element instanceof PsiType) {
            return "type";
        }

        return "unknown type";
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
        if (element instanceof PsiQualifiedNamedElement) {
            String qualifiedName = ((PsiQualifiedNamedElement) element).getQualifiedName();
            if (qualifiedName != null) {
                return qualifiedName;
            }
        }
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }

        return "";
    }
}
