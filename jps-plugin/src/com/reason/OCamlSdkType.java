package com.reason;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import gnu.trove.Equality;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCamlSdkType extends SdkType {

    public static final String ID = "OCaml SDK";
    private static final Pattern VERSION_REGEXP = Pattern.compile(".*(\\d\\.\\d\\d).*");

    public OCamlSdkType() {
        super(ID);
    }

    @Nullable
    public static Sdk getSDK(@NotNull Project project) {
        Sdk projectSDK = ProjectRootManager.getInstance(project).getProjectSdk();
        return projectSDK != null && ID.equals(projectSDK.getSdkType().getName()) ? projectSDK : null;
    }

    @NotNull
    public static SdkType getInstance() {
        return SdkType.findInstance(OCamlSdkType.class);
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "OCaml";
    }

    @NotNull
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

    @NotNull
    @Override
    public String getVersionString(@NotNull String sdkHome) {
        Matcher matcher = VERSION_REGEXP.matcher(sdkHome);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "unknown version";
    }

    @NotNull
    @Override
    public String suggestSdkName(String currentSdkName, @NotNull String sdkHome) {
        return new File(sdkHome).getName();
    }

    @Override
    public boolean isRootTypeApplicable(@NotNull OrderRootType type) {
        return type.name().equals("OCAML_SOURCES");
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
    }

    // Hack to get OCaml sources indexed like java sources
    // Find a better way to do it !!
    public static void reindexSourceRoots(@NotNull Sdk sdk) {
        VirtualFile[] ocamlSources = sdk.getRootProvider().getFiles(OCamlSourcesOrderRootType.getInstance());
        VirtualFile[] javaSources = sdk.getRootProvider().getFiles(OrderRootType.SOURCES);
        boolean equals = ArrayUtil.equals(ocamlSources, javaSources, (Equality<VirtualFile>) (v1, v2) -> v1.getPath().equals(v2.getPath()));

        if (!equals) {
            SdkModificator sdkModificator = sdk.getSdkModificator();

            sdkModificator.removeRoots(OrderRootType.SOURCES);
            for (VirtualFile root : ocamlSources) {
                sdkModificator.addRoot(root, OrderRootType.SOURCES);
            }

            ApplicationManager.getApplication().runWriteAction(sdkModificator::commitChanges);
        }
    }

}
