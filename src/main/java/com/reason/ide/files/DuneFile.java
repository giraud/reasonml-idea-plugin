package com.reason.ide.files;

import com.intellij.extapi.psi.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
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

    public @Nullable RPsiDuneStanza getStanza(@NotNull String name) {
        for (RPsiDuneStanza stanza : ORUtil.findImmediateChildrenOfClass(this, RPsiDuneStanza.class)) {
            if (name.equals(stanza.getName())) {
                return stanza;
            }
        }
        return null;
    }
}
