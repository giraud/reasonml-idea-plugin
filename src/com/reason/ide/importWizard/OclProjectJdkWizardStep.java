package com.reason.ide.importWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.reason.Log;
import com.reason.OCamlSdkType;
import com.reason.OCamlSourcesOrderRootType;
import com.reason.Platform;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

// com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep
public class OclProjectJdkWizardStep extends ModuleWizardStep {

    private static String[] SDKS = new String[]{"4.02.3", "4.03.0", "4.04.2", "4.05.0", "4.06.1", "4.07.1", "4.08.1", "4.09.0"};

    private static final Log LOG = Log.create("import.sdk");

    private JPanel c_pnlRoot;
    private JRadioButton c_rdSelectExisting;
    private JRadioButton c_rdDownloadSdk;
    private JComboBox<String> c_selDownload;
    private JdkComboBox c_selExistingSdk;
    private TextFieldWithBrowseButton textFieldWithBrowseButton1;

    private final WizardContext m_context;

    OclProjectJdkWizardStep(final WizardContext context) {
        this(context, null);
    }

    private OclProjectJdkWizardStep(WizardContext context, @Nullable @NonNls String helpId) {
        m_context = context;
    }

    private void createUIComponents() {
        ProjectSdksModel model = new ProjectSdksModel();
        model.reset(ProjectManager.getInstance().getDefaultProject());
        c_selExistingSdk = new JdkComboBox(model, sdkTypeId -> OCamlSdkType.ID.equals(sdkTypeId.getName()));
    }

    @Override
    public void _init() {
        for (String sdk : SDKS) {
            c_selDownload.addItem(sdk);
        }
    }

    @Override
    public JComponent getComponent() {
        return c_pnlRoot;
    }

    @Override
    public void updateDataModel() {
    }

    @Override
    public void onWizardFinished() throws CommitStepException {
        // not now ! because of navigation !
        if (c_rdDownloadSdk.isSelected()) {
            String selectedSdk = (String) c_selDownload.getSelectedItem();
            if (selectedSdk != null) {
                int pos = selectedSdk.lastIndexOf('.');
                String major = selectedSdk.substring(0, pos);
                String minor = selectedSdk.substring(pos + 1);

                // Download SDK from distribution site
                LOG.debug("Download SDK", selectedSdk);
                ProgressManager.getInstance().run(new SdkDownloader(major, minor, m_context.getProject()));

                // Create SDK !
                LOG.debug("Create SDK", selectedSdk);
                File targetSdkLocation = new File(Platform.getPluginLocation(), "ocaml-" + selectedSdk);
                Sdk odk = SdkConfigurationUtil.createAndAddSDK(targetSdkLocation.getAbsolutePath(), new OCamlSdkType());
                if (odk != null) {
                    SdkModificator odkModificator = odk.getSdkModificator();

                    odkModificator.setVersionString(selectedSdk);  // must be set after home path, otherwise setting home path clears the version string
                    odkModificator.setName("OCaml (sources only) " + major);
                    try {
                        addSdkSources(odkModificator, targetSdkLocation);
                    } catch (IOException e) {
                        throw new CommitStepException(e.getMessage());
                    }

                    odkModificator.commitChanges();

                    // update selected sdk in builder
                    ProjectBuilder builder = m_context.getProjectBuilder();
                    if (builder instanceof DuneProjectImportBuilder) {
                        ((DuneProjectImportBuilder) builder).setModuleSdk(odk);
                    }
                }
            }
        }
    }

    private void addSdkSources(@NotNull SdkModificator odkModificator, @NotNull File targetSdkLocation) throws IOException {
        VirtualFileSystem fileSystem = VirtualFileManager.getInstance().getFileSystem(StandardFileSystems.FILE_PROTOCOL);

        Files.walkFileTree(targetSdkLocation.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
                VirtualFile file = fileSystem.findFileByPath(path.toString());
                if (file != null) {
                    odkModificator.addRoot(file, OCamlSourcesOrderRootType.getInstance());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
