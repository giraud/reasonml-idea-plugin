package com.reason.lang.dune;

import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class DuneVarParsingTest extends DuneParsingTestCase {
    @Test
    public void test_basic() {
        RPsiDuneVar e = firstOfType(parseRawCode("%{x}"), RPsiDuneVar.class);

        assertEquals("%{x}", e.getText());
    }
}
