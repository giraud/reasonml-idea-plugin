package com.reason;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCamlSDK extends SdkType {

    public static final String ID = "OCaml SDK";
    private static final Pattern VERSION_REGEXP = Pattern.compile(".*(\\d\\.\\d\\d).*");

    public OCamlSDK() {
        super(ID);
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
        return Icons.OCL_SDK;
    }

    @NotNull
    @Override
    public Icon getIconForAddAction() {
        return Icons.OCL_SDK;
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
    public String getVersionString(@NotNull String sdkHome) {
        Matcher matcher = VERSION_REGEXP.matcher(sdkHome);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    @NotNull
    @Override
    public String suggestSdkName(String currentSdkName, @NotNull String sdkHome) {
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
