package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
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

    public static void processModules(@NotNull Project project, @Nullable GlobalSearchScope scope, @NotNull IndexKeys.ProcessElement<PsiFakeModule> processor) {
        StubIndex.getInstance().processAllKeys(IndexKeys.MODULES_TOP_LEVEL, project,
                name -> {
                    Collection<PsiFakeModule> collection = getModules(name, project, scope);
                    for (PsiFakeModule module : collection) {
                        processor.process(module);
                    }
                    return true;
                });
    }

    public static @NotNull Collection<PsiFakeModule> getModules(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.MODULES_TOP_LEVEL, key, project, scope, PsiFakeModule.class);
    }
}
