package com.reason.ide.search;

import org.jetbrains.annotations.Nullable;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.tree.TokenSet;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.napkin.NsLexer;
import com.reason.lang.napkin.NsTypes;

public class NsFindUsagesProvider extends ORFindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        ORTypes types = NsTypes.INSTANCE;
        return new DefaultWordsScanner(new NsLexer(), //
                                       TokenSet.create(types.UIDENT, types.LIDENT, types.VARIANT_NAME), //
                                       TokenSet.EMPTY, //
                                       TokenSet.create(types.FLOAT_VALUE, types.INT_VALUE, types.STRING_VALUE));
    }
}
