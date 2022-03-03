package com.reason.ide.files;

import com.intellij.extapi.psi.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

public class DuneFile extends PsiFileBase {
    public DuneFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, DuneLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return DuneFileType.INSTANCE;
    }

    public @Nullable PsiStanza getStanza(@NotNull String name) {
        for (PsiStanza stanza : ORUtil.findImmediateChildrenOfClass(this, PsiStanza.class)) {
            if (name.equals(stanza.getName())) {
                return stanza;
            }
        }
        return null;
    }
}
