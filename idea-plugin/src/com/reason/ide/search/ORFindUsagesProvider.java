package com.reason.ide.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lexer.FlexAdapter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import com.reason.lang.core.type.ORTypes;

public abstract class ORFindUsagesProvider implements com.intellij.lang.findUsages.FindUsagesProvider {

    private final FlexAdapter m_lexer;
    private final ORTypes m_types;

    protected ORFindUsagesProvider(FlexAdapter lexer, ORTypes types) {
        m_lexer = lexer;
        m_types = types;
    }

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(m_lexer, //
                                       TokenSet.create((IElementType) m_types.C_UPPER_IDENTIFIER, (IElementType) m_types.C_LOWER_IDENTIFIER), //
                                       TokenSet.EMPTY, //
                                       TokenSet.create((IElementType) m_types.C_UPPER_SYMBOL, (IElementType) m_types.C_LOWER_SYMBOL));
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof PsiUpperIdentifier || element instanceof PsiLowerIdentifier;
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
        if (element instanceof PsiModule) {
            return "Module " + ((PsiModule) element).getName();
        } else if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            return name == null ? "" : name;
        }

        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiQualifiedElement) {
            return ((PsiQualifiedElement) element).getQualifiedName();
        }
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }

        return element.getText();
    }
}
