package com.reason.ide.search;

import com.reason.lang.reason.RmlLexer;
import com.reason.lang.reason.RmlTypes;

public class RmlFindUsagesProvider extends ORFindUsagesProvider {
    protected RmlFindUsagesProvider() {
        super(new RmlLexer(), RmlTypes.INSTANCE);
    }
}
