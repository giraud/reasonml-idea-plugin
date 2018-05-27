package com.reason.ide.sdk;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ProjectRootManager;
import com.reason.icons.Icons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class OCamlSDK extends SdkType {

    public OCamlSDK() {
        super("OCaml SDK");
    }

    @Nullable
    public static Sdk getSDK(@NotNull Project project) {
        Sdk projectSDK = ProjectRootManager.getInstance(project).getProjectSdk();
        if (projectSDK != null && projectSDK.getSdkType().getName().equals("OCaml SDK")) {
            return projectSDK;
        }
        return null;
    }

    @Override
    public Icon getIcon() {
        return Icons.OCL_FILE;
    }

    @NotNull
    @Override
    public Icon getIconForAddAction() {
        return Icons.OCL_FILE;
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        return null;
    }

    @Override
    public boolean isValidSdkHome(String path) {
        return true;
    }

    @Nullable
    @Override
    public String getVersionString(String sdkHome) {
        return "4.02";
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return new File(sdkHome).getName();
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "OCaml";
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {

    }
}
