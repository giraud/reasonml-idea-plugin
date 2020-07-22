package com.reason.lang.napkin;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.type.ORTypes;

public abstract class NsParsingTestCase extends BaseParsingTestCase {
    public ORTypes m_types = NsTypes.INSTANCE;

    public NsParsingTestCase() {
        super("", "res", new NsParserDefinition());
    }
}
