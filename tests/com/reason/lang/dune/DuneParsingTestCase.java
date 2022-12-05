package com.reason.lang.dune;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.*;
import com.reason.lang.core.stub.*;

abstract class DuneParsingTestCase extends BaseParsingTestCase {
    public DuneTypes myTypes = DuneTypes.INSTANCE;

    protected DuneParsingTestCase() {
        super("", "", new DuneParserDefinition());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StubElementTypeHolderEP stubElementTypeHolderEP = new StubElementTypeHolderEP();
        stubElementTypeHolderEP.holderClass = ResStubBasedElementTypes.class.getName();
        registerExtension(StubElementTypeHolderEP.EP_NAME, stubElementTypeHolderEP);
        LanguageASTFactory.INSTANCE.addExplicitExtension(DuneLanguage.INSTANCE, new DuneASTFactory());
    }

    protected ORLanguageProperties getLangProps() {
        return ORLanguageProperties.cast(myLanguage);
    }
}
