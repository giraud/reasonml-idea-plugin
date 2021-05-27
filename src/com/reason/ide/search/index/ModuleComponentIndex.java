package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class ModuleComponentIndex extends StringStubIndexExtension<PsiModule> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiModuleStubElementType.VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiModule> getKey() {
        return IndexKeys.MODULES_COMP;
    }
}
