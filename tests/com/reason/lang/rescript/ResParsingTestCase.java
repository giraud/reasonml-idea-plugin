package com.reason.lang.rescript;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;

abstract class ResParsingTestCase extends BaseParsingTestCase {
    public ORTypes myTypes = ResTypes.INSTANCE;

    protected ResParsingTestCase() {
        super("", "res", new ResParserDefinition());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StubElementTypeHolderEP stubElementTypeHolderEP = new StubElementTypeHolderEP();
        stubElementTypeHolderEP.holderClass = ResStubBasedElementTypes.class.getName();
        registerExtension(StubElementTypeHolderEP.EP_NAME, stubElementTypeHolderEP);
        LanguageASTFactory.INSTANCE.addExplicitExtension(ResLanguage.INSTANCE, new ResASTFactory());
    }

    protected ORLanguageProperties getLangProps() {
        return ORLanguageProperties.cast(myLanguage);
    }
}
