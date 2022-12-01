package com.reason.ide.library;

import com.intellij.codeInsight.daemon.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.ui.*;
import com.reason.comp.dune.*;
import com.reason.ide.files.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.event.*;
import java.util.*;

/**
 * If the user open a file (.ml/.mli => FileHelper.isOCaml) and the SDK isn't set,
 * then he/she will see a message to set up the SDK, along with a button to fix this error.
 */
public class OclSdkSetupValidator implements ProjectSdkSetupValidator {
    public static final String CONFIGURE_OCAML_SDK = "Please configure the Opam switch";

    @Override
    public boolean isApplicableFor(@NotNull Project project, @NotNull VirtualFile file) {
        return FileHelper.isOCaml(file.getFileType());
    }

    @Override
    public @Nullable String getErrorMessage(@NotNull Project project, @NotNull VirtualFile file) {
        // Checking for Opam switch
        ORSettings orSettings = project.getService(ORSettings.class);
        if (orSettings.getSwitchName().isEmpty()) {
            Map<Module, VirtualFile> contentRootsFor = Platform.findContentRootsFor(project, DunePlatform.DUNE_PROJECT_FILENAME);
            if (!contentRootsFor.isEmpty()) {
                // Error: this is a dune project, and no opam switch is selected
                return CONFIGURE_OCAML_SDK;
            }
        }

        return null;
    }

    @Override
    public @NotNull EditorNotificationPanel.ActionHandler getFixHandler(@NotNull Project project, @NotNull VirtualFile file) {
        return new EditorNotificationPanel.ActionHandler() {
            @Override
            public void handlePanelActionClick(@NotNull EditorNotificationPanel panel, @NotNull HyperlinkEvent event) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, ORSettingsConfigurable.class);
            }

            @Override
            public void handleQuickFixClick(@NotNull Editor editor, @NotNull PsiFile psiFile) {
            }
        };
    }
}
