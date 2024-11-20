package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class PsiModuleStub extends NamedStubBase<RPsiModule> {
    private final String[] myPath;
    private final String myQname;
    private final String myAlias;
    private final boolean myIsComponent;
    private final boolean myIsModuleType;
    private final boolean myIsTopLevel;
    private final boolean myIsFunctorCall;
    private final String myReturnTypeName;

    public PsiModuleStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name,
                         @Nullable String[] path, @Nullable String namespace, String alias, boolean isComponent,
                         boolean isModuleType, boolean isTopLevel, boolean isFunctorCall, @Nullable String returnTypeName) {
        super(parent, elementType, name);
        myPath = path;
        myQname = namespace == null ? (path != null && path.length > 0 ? Joiner.join(".", path) + "." + name : name) : namespace;
        myAlias = alias;
        myIsComponent = isComponent;
        myIsModuleType = isModuleType;
        myIsTopLevel = isTopLevel;
        myIsFunctorCall = isFunctorCall;
        myReturnTypeName = returnTypeName;
    }

    public PsiModuleStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name,
                         @Nullable String[] path, @Nullable String namespace, String alias, boolean isComponent,
                         boolean isModuleType, boolean isTopLevel, boolean isFunctorCall, @Nullable StringRef returnTypeNameRef) {
        super(parent, elementType, name);
        myPath = path;
        myQname = namespace == null ? path != null && path.length > 0 ? Joiner.join(".", path) + "." + name : "" + name : namespace;
        myAlias = alias;
        myIsComponent = isComponent;
        myIsModuleType = isModuleType;
        myIsTopLevel = isTopLevel;
        myIsFunctorCall = isFunctorCall;
        myReturnTypeName = returnTypeNameRef != null ? returnTypeNameRef.getString() : null;
    }

    public @Nullable String[] getPath() {
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

    public boolean isModuleType() {
        return myIsModuleType;
    }

    public boolean isTopLevel() {
        return myIsTopLevel;
    }

    public boolean isFunctorCall() {
        return myIsFunctorCall;
    }

    public String getSignatureName() {
        return myReturnTypeName;
    }
}
