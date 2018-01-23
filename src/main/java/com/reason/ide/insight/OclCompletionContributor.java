package com.reason.ide.insight;

import com.reason.lang.ocaml.OclTypes;

public class OclCompletionContributor extends CompletionContributor {
    OclCompletionContributor() {
        super(OclTypes.INSTANCE);
    }
}
