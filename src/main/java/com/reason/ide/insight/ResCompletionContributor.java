package com.reason.ide.insight;

import com.reason.lang.rescript.*;

public class ResCompletionContributor extends ORCompletionContributor {
    ResCompletionContributor() {
        super(ResTypes.INSTANCE, ResQNameFinder.INSTANCE);
    }
}
