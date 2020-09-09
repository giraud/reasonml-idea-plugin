package com.reason.ide.search;

import com.reason.lang.napkin.NsLexer;
import com.reason.lang.napkin.NsTypes;

public class NsFindUsagesProvider extends ORFindUsagesProvider {
    protected NsFindUsagesProvider() {
        super(new NsLexer(), NsTypes.INSTANCE);
    }
}
