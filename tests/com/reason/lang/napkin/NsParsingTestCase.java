package com.reason.lang.napkin;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.psi.stubs.StubElementTypeHolderEP;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.stub.RescriptStubBasedElementTypes;
import com.reason.lang.core.type.ORTypes;

public abstract class NsParsingTestCase extends BaseParsingTestCase {
  public ORTypes m_types = ResTypes.INSTANCE;

  public NsParsingTestCase() {
    super("", "res", new ResParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    StubElementTypeHolderEP stubElementTypeHolderEP = new StubElementTypeHolderEP();
    stubElementTypeHolderEP.holderClass = RescriptStubBasedElementTypes.class.getName();
    registerExtension(StubElementTypeHolderEP.EP_NAME, stubElementTypeHolderEP);
    LanguageASTFactory.INSTANCE.addExplicitExtension(ResLanguage.INSTANCE, new ResASTFactory());
  }
}
