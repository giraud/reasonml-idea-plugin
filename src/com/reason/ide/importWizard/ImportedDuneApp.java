package com.reason.ide.importWizard;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class ImportedDuneApp {
    @NotNull
    private final String m_name;

    public ImportedDuneApp(@NotNull VirtualFile root, @NotNull VirtualFile duneConfig) {
        m_name = "DUNE";
    }
}
