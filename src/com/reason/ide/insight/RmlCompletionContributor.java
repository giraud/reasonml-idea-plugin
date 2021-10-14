package com.reason.ide.insight;

import com.reason.lang.reason.*;

public class RmlCompletionContributor extends ORCompletionContributor {
    RmlCompletionContributor() {
        super(RmlTypes.INSTANCE, RmlQNameFinder.INSTANCE);
    }
}
