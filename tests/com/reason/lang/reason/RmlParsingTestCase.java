package com.reason.lang.reason;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.psi.stubs.StubElementTypeHolderEP;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.stub.RmlStubBasedElementTypes;
import com.reason.lang.core.type.ORTypes;

abstract class RmlParsingTestCase extends BaseParsingTestCase {
  public ORTypes m_types = RmlTypes.INSTANCE;

  public RmlParsingTestCase() {
    super("testData/com/reason/lang", "re", new RmlParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    StubElementTypeHolderEP stubElementTypeHolderEP = new StubElementTypeHolderEP();
    stubElementTypeHolderEP.holderClass = RmlStubBasedElementTypes.class.getName();
    registerExtension(StubElementTypeHolderEP.EP_NAME, stubElementTypeHolderEP);
    LanguageASTFactory.INSTANCE.addExplicitExtension(RmlLanguage.INSTANCE, new RmlASTFactory());
  }
}
