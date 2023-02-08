package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class PsiModuleStub extends NamedStubBase<RPsiModule> {
    private final String[] myPath;
    private final String[] myQualifiedNameAsPath;
    private final String myQname;
    private final String myAlias;
    private final boolean myIsComponent;
    private final boolean myIsInterface;
    private final boolean myIsTopLevel;
    private final boolean myIsFunctorCall;

    public PsiModuleStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name,
                         String @Nullable [] path, @Nullable String[] qNamePath, @Nullable String namespace, String alias, boolean isComponent,
                         boolean isInterface, boolean isTopLevel, boolean isFunctorCall) {
        super(parent, elementType, name);
        myPath = path;
        myQualifiedNameAsPath = qNamePath;
        myQname = namespace == null ? path != null && path.length > 0 ? Joiner.join(".", path) + "." + name : "" + name : namespace;
        myAlias = alias;
        myIsComponent = isComponent;
        myIsInterface = isInterface;
        myIsTopLevel = isTopLevel;
        myIsFunctorCall = isFunctorCall;
    }

    public PsiModuleStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name,
                         String @Nullable [] path, @Nullable String[] qNamePath, @Nullable String namespace, String alias, boolean isComponent,
                         boolean isInterface, boolean isTopLevel, boolean isFunctorCall) {
        super(parent, elementType, name);
        myPath = path;
        myQualifiedNameAsPath = qNamePath;
        myQname = namespace == null ? path != null && path.length > 0 ? Joiner.join(".", path) + "." + name : "" + name : namespace;
        myAlias = alias;
        myIsComponent = isComponent;
        myIsInterface = isInterface;
        myIsTopLevel = isTopLevel;
        myIsFunctorCall = isFunctorCall;
    }

    public String @Nullable [] getPath() {
        return myPath;
    }

    public @NotNull String getQualifiedName() {
        return myQname;
    }

    public String getAlias() {
        return myAlias;
    }

    public boolean isComponent() {
        return myIsComponent;
    }

    public boolean isInterface() {
        return myIsInterface;
    }

    public boolean isTopLevel() {
        return myIsTopLevel;
    }

    public boolean isFunctorCall() {
        return myIsFunctorCall;
    }

    public @Nullable String[] getQualifiedNameAsPath() {
        return myQualifiedNameAsPath;
    }
}
