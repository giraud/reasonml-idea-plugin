package com.reason.ide.search;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.tree.TokenSet;
import com.reason.lang.core.type.ORLangTypes;
import com.reason.lang.rescript.ResLexer;
import com.reason.lang.rescript.ResTypes;
import org.jetbrains.annotations.Nullable;

public class ResFindUsagesProvider extends ORFindUsagesProvider {
    @Override
    public @Nullable WordsScanner getWordsScanner() {
        ORLangTypes types = ResTypes.INSTANCE;
        return new DefaultWordsScanner(
                new ResLexer(), //
                TokenSet.create(types.UIDENT, types.LIDENT, types.A_MODULE_NAME, types.A_VARIANT_NAME), //
                TokenSet.create(types.MULTI_COMMENT, types.SINGLE_COMMENT), //
                TokenSet.create(types.FLOAT_VALUE, types.INT_VALUE, types.STRING_VALUE));
    }
}
