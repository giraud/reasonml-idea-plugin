package jpsplugin.com.reason.sdk;

import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import icons.*;
import jpsplugin.com.reason.*;
import org.jdom.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class OCamlSdkType extends SdkType {

    public static final String ID = "OCaml SDK";
    private static final Pattern VERSION_REGEXP = Pattern.compile(".*/(\\d\\.\\d\\d(\\.\\d)?[^/]*)/?.*");
    public static final Comparator<VirtualFile> SAME_FILE = Comparator.comparing(VirtualFile::getPath);

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
        return ORIcons.OCL_SDK;
    }

    @NotNull
    @Override
    public Icon getIconForAddAction() {
        return ORIcons.OCL_SDK;
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

    // Hack to get OCaml sources indexed like java sources
    // Find a better way to do it !!
    public static void reindexSourceRoots(@Nullable Sdk odk) {
        if (odk != null) {
            VirtualFile[] ocamlSources = odk.getRootProvider().getFiles(OCamlSourcesOrderRootType.getInstance());
            VirtualFile[] javaSources = odk.getRootProvider().getFiles(OrderRootType.SOURCES);

            if (!Arrays.equals(ocamlSources, javaSources, SAME_FILE)) {
                SdkModificator sdkModificator = odk.getSdkModificator();

                sdkModificator.removeRoots(OrderRootType.SOURCES);
                for (VirtualFile root : ocamlSources) {
                    sdkModificator.addRoot(root, OrderRootType.SOURCES);
                }

                Application application = ApplicationManager.getApplication();
                application.invokeLater(() -> application.runWriteAction(sdkModificator::commitChanges));
            }
        }
    }

    @Override
    public @Nullable AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return new OCamlAdditionalDataConfigurable();
    }

    @Override
    public @Nullable SdkAdditionalData loadAdditionalData(@NotNull Element additional) {
        OCamlSdkAdditionalData odkData = new OCamlSdkAdditionalData();
        odkData.setMajor(additional.getAttributeValue("major"));
        odkData.setMinor(additional.getAttributeValue("minor"));
        odkData.setPatch(additional.getAttributeValue("patch"));
        odkData.setForced(Boolean.parseBoolean(additional.getAttributeValue("forced")));
        odkData.setCygwin(Boolean.parseBoolean(additional.getAttributeValue("cygwin")));
        odkData.setCygwinBash(additional.getAttributeValue("cygwinBash"));
        return odkData;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData data, @NotNull Element additional) {
        OCamlSdkAdditionalData odkData = (OCamlSdkAdditionalData) data;
        additional.setAttribute("major", odkData.getMajor());
        additional.setAttribute("minor", odkData.getMinor());
        additional.setAttribute("patch", odkData.getPatch());
        additional.setAttribute("forced", odkData.isForced().toString());
        additional.setAttribute("cygwin", Boolean.toString(odkData.isCygwin()));
        additional.setAttribute("cygwinBash", odkData.getCygwinBash());
    }
}
