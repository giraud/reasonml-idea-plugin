package com.reason.ide.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lexer.LexerBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.RmlLexerAdapter;
import com.reason.lang.RmlTypes;
import com.reason.lang.core.psi.PsiNamedElement;

public class FindUsagesProvider implements com.intellij.lang.findUsages.FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return (fileText, processor) -> {
            LexerBase lexer = new RmlLexerAdapter();
            lexer.start(fileText);
            IElementType tokenType;
            while ((tokenType = lexer.getTokenType()) != null) {
                //TODO process occurrences in string literals and comments
                if (tokenType == RmlTypes.LIDENT || tokenType == RmlTypes.UIDENT || tokenType == RmlTypes.MODULE_NAME || tokenType == RmlTypes.VALUE_NAME) {
                    int tokenStart = lexer.getTokenStart();
                    for (TextRange wordRange : StringUtil.getWordIndicesIn(lexer.getTokenText())) {
                        int start = tokenStart + wordRange.getStartOffset();
                        int end = tokenStart + wordRange.getEndOffset();
                        System.out.println("scan: " + start + "," + end + " -> " + lexer.getTokenText());
                        processor.process(new WordOccurrence(fileText, start, end, WordOccurrence.Kind.CODE));
                    }
                }
                lexer.advance();
            }
        };
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return true; // element instanceof Module/* || element instanceof PsiExternal || element instanceof PsiType*/;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        return "module";
        //return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return "module descriptive name";
        //return ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return ((PsiNamedElement) element).getName();
        //return ElementDescriptionUtil.getElementDescription(element, UsageViewNodeTextLocation.INSTANCE);
    }
}
