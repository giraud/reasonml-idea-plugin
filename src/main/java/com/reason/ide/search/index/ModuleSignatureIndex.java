package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Module indexed by the *name* of the signature (module type)
 */
public class ModuleSignatureIndex extends StringStubIndexExtension<RPsiModule> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.MODULE;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiModule> getKey() {
        return IndexKeys.MODULES_SIGNATURE;
    }

    public static @NotNull Collection<RPsiModule> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.MODULES_SIGNATURE, key, project, scope, RPsiModule.class);
    }
}
