package com.reason.lang.dune;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.DuneFile;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiDuneVersion;

public class VersionTest extends BaseParsingTestCase {
    public VersionTest() {
        super("", "", new DuneParserDefinition());
    }

    public void testVersion() {
        DuneFile e = parseDuneCode("(jbuild_version 1)");

//        PsiDuneVersion version = PsiTreeUtil.findChildOfType(e, PsiDuneVersion.class);
//        assertNotNull(version);
    }

}
