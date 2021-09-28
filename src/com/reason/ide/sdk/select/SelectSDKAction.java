package com.reason.ide.sdk.select;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.files.*;
import com.reason.ide.sdk.*;
import org.jetbrains.annotations.*;

import java.awt.event.*;

/**
 * This class is using SdkPopupFactory to create
 * the popup to select the version of OCaml. Everything related to the download or
 * the detected SDK is loaded from OCamlSDKType. Here
 * - I'm filtering so that only OCaml SDK are printed
 * - I'm using SdkPopupFactory so the project is automatically reloaded with the new SDK
 *
 * @see OCamlSDKValidator (this popup loaded inside the fileview)
 * @see OCamlSdkType (download, detect SDK, filter SDKs, ...)
 */
public class SelectSDKAction extends AnAction {

    private JBPopup myPopup;

    public SelectSDKAction() {
        this.setEnabledInModalContext(true);
    }

    /**
     * Used to create the popup inside the editor (see {@link OCamlSDKValidator})
     * or to create the popup that this class is creating.
     */
    public static SdkPopupBuilder newBuilder(Project project) {
        return SdkPopupFactory.newBuilder().withProject(project)
                .withSdkTypeFilter(c -> c.getName().equals(OCamlSdkType.ID))
                .updateProjectSdkFromSelection();
    }

    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(isOCamlFile(e));
    }

    private boolean isOCamlFile(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return false;
        }
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file == null) {
            return false;
        }
        return FileHelper.isOCaml(file.getFileType());
    }

    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        if (project != null) {
            if (myPopup != null && myPopup.isVisible()) {
                myPopup.cancel();
            } else {
                // show the popup, next to build, run, ...
                // or in the middle of the screen if there is no event
                myPopup = newBuilder(project).buildPopup();

                InputEvent event = e.getInputEvent();
                if (event != null) {
                    myPopup.showUnderneathOf(e.getInputEvent().getComponent());
                } else {
                    myPopup.showCenteredInCurrentWindow(project);
                }
            }
        }
    }
}
