package com.reason.ide.search;

import com.reason.lang.ocaml.OclLexer;
import com.reason.lang.ocaml.OclTypes;

public class OclFindUsagesProvider extends ORFindUsagesProvider {
    protected OclFindUsagesProvider() {
        super(new OclLexer(), OclTypes.INSTANCE);
    }
}
