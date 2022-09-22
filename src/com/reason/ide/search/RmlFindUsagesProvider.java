package com.reason.ide.search;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.tree.TokenSet;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.reason.RmlLexer;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.Nullable;

public class RmlFindUsagesProvider extends ORFindUsagesProvider {
    @Override
    public @Nullable WordsScanner getWordsScanner() {
        ORTypes types = RmlTypes.INSTANCE;
        return new DefaultWordsScanner(
                new RmlLexer(), //
                TokenSet.create(types.UIDENT, types.LIDENT, types.A_MODULE_NAME, types.A_VARIANT_NAME), //
                TokenSet.create(types.MULTI_COMMENT, types.SINGLE_COMMENT), //
                TokenSet.create(types.FLOAT_VALUE, types.INT_VALUE, types.STRING_VALUE));
    }
}
