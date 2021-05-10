package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ModuleTopLevelIndex extends StringStubIndexExtension<PsiFakeModule> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiFakeModuleStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiFakeModule> getKey() {
        return IndexKeys.MODULES_TOP_LEVEL;
    }

    public static @NotNull Collection<PsiFakeModule> getElements(String key, Project project) {
        return StubIndex.getElements(IndexKeys.MODULES_TOP_LEVEL, key, project, null, PsiFakeModule.class);
    }
}
