package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.type.ORTypes;

public abstract class RmlParsingTestCase extends BaseParsingTestCase {
    public ORTypes m_types = RmlTypes.INSTANCE;

    public RmlParsingTestCase() {
        super("", "re", new RmlParserDefinition());
    }
}
