package com.reason.lang.dune;

import com.reason.ide.files.DuneFile;
import com.reason.lang.BaseParsingTestCase;

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
