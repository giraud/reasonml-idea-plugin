package com.reason.ide.insight;

import com.reason.lang.ocaml.*;

public class OclCompletionContributor extends ORCompletionContributor {
    OclCompletionContributor() {
        super(OclTypes.INSTANCE, OclQNameFinder.INSTANCE);
    }
}
