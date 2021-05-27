package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ModuleFqnIndex extends IntStubIndexExtension<PsiModule> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiModuleStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiModule> getKey() {
        return IndexKeys.MODULES_FQN;
    }

    public static @NotNull Collection<PsiModule> getElements(@NotNull String qname, @NotNull Project project) {
        return StubIndex.getElements(IndexKeys.MODULES_FQN, qname.hashCode(), project, null, PsiModule.class);
    }
}
