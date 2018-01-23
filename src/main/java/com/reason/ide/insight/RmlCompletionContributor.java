package com.reason.ide.insight;

import com.reason.lang.reason.RmlTypes;

public class RmlCompletionContributor extends CompletionContributor {
    RmlCompletionContributor() {
        super(RmlTypes.INSTANCE);
    }
}
