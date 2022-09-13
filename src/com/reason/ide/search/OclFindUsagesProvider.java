package com.reason.ide.search;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.tree.TokenSet;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclLexer;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.Nullable;

public class OclFindUsagesProvider extends ORFindUsagesProvider {
    @Override
    public @Nullable WordsScanner getWordsScanner() {
        ORTypes types = OclTypes.INSTANCE;
        return new DefaultWordsScanner(
                new OclLexer(), //
                TokenSet.create(types.UIDENT, types.LIDENT), //
                TokenSet.create(types.MULTI_COMMENT), //
                TokenSet.create(types.FLOAT_VALUE, types.INT_VALUE, types.STRING_VALUE));
    }
}
