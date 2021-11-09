package com.reason.lang.ocaml;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;

abstract class OclParsingTestCase extends BaseParsingTestCase {
    public ORTypes m_types = OclTypes.INSTANCE;

    protected OclParsingTestCase() {
        super("", "ml", new OclParserDefinition());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StubElementTypeHolderEP stubElementTypeHolderEP = new StubElementTypeHolderEP();
        stubElementTypeHolderEP.holderClass = OclStubBasedElementTypes.class.getName();
        registerExtension(StubElementTypeHolderEP.EP_NAME, stubElementTypeHolderEP);
        LanguageASTFactory.INSTANCE.addExplicitExtension(OclLanguage.INSTANCE, new OclASTFactory());
    }

    protected ORLanguageProperties getLangProps() {
        return ORLanguageProperties.cast(myLanguage);
    }
}
