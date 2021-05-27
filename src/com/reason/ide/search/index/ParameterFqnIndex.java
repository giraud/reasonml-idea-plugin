package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ParameterFqnIndex extends IntStubIndexExtension<PsiParameter> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiParameterStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiParameter> getKey() {
        return IndexKeys.PARAMETERS_FQN;
    }

    public static @NotNull Collection<PsiParameter> getElements(String key, Project project) {
        return StubIndex.getElements(IndexKeys.PARAMETERS_FQN, key.hashCode(), project, null, PsiParameter.class);
    }
}
