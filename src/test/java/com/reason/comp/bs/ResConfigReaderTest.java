package com.reason.comp.bs;

import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

// All other tests in BsConfigReaderTest
@RunWith(JUnit4.class)
public class ResConfigReaderTest extends ORBasePlatformTestCase {
    @Test
    public void testJsx_4() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x', 'jsx': {'version': 4}}"));
        assertEquals("4", bsConfig.getJsxVersion());
        assertNull(bsConfig.getJsxMode());

        BsConfig bsConfig1 = BsConfigReader.parse(toJson("{'name': 'x', 'jsx': {'version': 4, 'mode': 'automatic'}}"));
        assertEquals("4", bsConfig1.getJsxVersion());
        assertEquals("automatic", bsConfig1.getJsxMode());
    }

    @Test
    public void testUncurried() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x', 'uncurried': false}"));
        assertFalse(bsConfig.isUncurried());
    }
}
