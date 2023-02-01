package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiParameterDeclarationStub extends NamedStubBase<RPsiParameterDeclaration> {
    private final String[] myPath;
    private final String myQname;
    private final boolean myIsNamed;

    public PsiParameterDeclarationStub(StubElement parent, @NotNull IStubElementType elementType, String name, String[] path, String qName, boolean isNamed) {
        super(parent, elementType, name);
        myPath = path;
        myQname = qName;
        myIsNamed = isNamed;
    }

    public PsiParameterDeclarationStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String[] path, String qname, boolean isNamed) {
        super(parent, elementType, name);
        myPath = path;
        myQname = qname;
        myIsNamed = isNamed;
    }

    public String[] getPath() {
        return myPath;
    }

    public String getQualifiedName() {
        return myQname;
    }

    public boolean isNamed() {
        return myIsNamed;
    }
}
