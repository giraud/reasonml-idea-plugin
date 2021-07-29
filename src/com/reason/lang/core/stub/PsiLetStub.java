package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiLetStub extends NamedStubBase<PsiLet> {
    private final String[] myPath;
    private final String myQname;
    private final String myAlias;
    private final boolean myIsFunction;
    private final List<String> myDeconstructionNames;

    public PsiLetStub(StubElement parent, @NotNull IStubElementType elementType, String name, String[] path, String alias, boolean isFunction, List<String> deconstructionNames) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
        myAlias = alias;
        myIsFunction = isFunction;
        myDeconstructionNames = deconstructionNames;
    }

    public PsiLetStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String[] path, String alias, boolean isFunction, List<String> deconstructionNames) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
        myAlias = alias;
        myIsFunction = isFunction;
        myDeconstructionNames = deconstructionNames;
    }

    public String[] getPath() {
        return myPath;
    }

    public String getQualifiedName() {
        return myQname;
    }

    public String getAlias() {
        return myAlias;
    }

    public boolean isFunction() {
        return myIsFunction;
    }

    public List<String> getDeconstructionNames() {
        return myDeconstructionNames;
    }
}
