package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class LetComponentFqnIndex extends IntStubIndexExtension<RPsiLet> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.LET;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RPsiLet> getKey() {
        return IndexKeys.LETS_COMP_FQN;
    }

    public static @NotNull Collection<RPsiLet> getElements(String qName, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.LETS_COMP_FQN, qName.hashCode(), project, scope, RPsiLet.class);
    }

    public static void processItems(@NotNull Project project, @Nullable GlobalSearchScope scope, @NotNull IndexKeys.ProcessElement<RPsiLet> processor) {
        StubIndex.getInstance().processAllKeys(IndexKeys.LETS_COMP_FQN, project,
                hashCode -> {
                    for (RPsiLet let : StubIndex.getElements(IndexKeys.LETS_COMP_FQN, hashCode, project, scope, RPsiLet.class)) {
                        processor.process(let);
                    }
                    return true;
                });
    }

}
