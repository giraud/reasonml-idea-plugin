package com.reason.lang.reason;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.psi.stubs.StubElementTypeHolderEP;
import com.reason.lang.*;
import com.reason.lang.core.stub.RmlStubBasedElementTypes;
import com.reason.lang.core.type.ORTypes;

abstract class RmlParsingTestCase extends BaseParsingTestCase {
    public ORTypes myTypes = RmlTypes.INSTANCE;

    protected RmlParsingTestCase() {
        super("", "re", new RmlParserDefinition());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StubElementTypeHolderEP stubElementTypeHolderEP = new StubElementTypeHolderEP();
        stubElementTypeHolderEP.holderClass = RmlStubBasedElementTypes.class.getName();
        registerExtension(StubElementTypeHolderEP.EP_NAME, stubElementTypeHolderEP);
        LanguageASTFactory.INSTANCE.addExplicitExtension(RmlLanguage.INSTANCE, new RmlASTFactory());
    }

    protected ORLanguageProperties getLangProps() {
        return ORLanguageProperties.cast(myLanguage);
    }
}
