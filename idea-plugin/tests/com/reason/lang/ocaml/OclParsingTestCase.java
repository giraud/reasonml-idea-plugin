package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.type.ORTypes;

public abstract class OclParsingTestCase extends BaseParsingTestCase {
    public ORTypes m_types = OclTypes.INSTANCE;

    public OclParsingTestCase() {
        super("", "ml", new OclParserDefinition());
    }
}