package com.reason.ide.importWizard;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.util.text.*;
import com.intellij.openapi.vfs.*;
import com.intellij.projectImport.*;
import com.reason.ide.settings.*;

import javax.swing.*;

public class DuneProjectRootStep extends ProjectImportWizardStep {
    private JPanel myPanel;
    private TextFieldWithBrowseButton myProjectRootComponent;
    private OpamConfigurationTab myOpamConfigurationTab;

    public DuneProjectRootStep(WizardContext context) {
        super(context);

        Project project = context.getProject();

        context.setProjectBuilder(ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(DuneProjectImportBuilder.class));

        //noinspection DialogTitleCapitalization
        myProjectRootComponent.addBrowseFolderListener("Select `dune-project` of a Dune project to import", "", null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor());
        myProjectRootComponent.setText(context.getProjectFileDirectory()); // provide project path

        myOpamConfigurationTab.createComponent(project, "");
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public boolean validate() {
        String projectRootPath = myProjectRootComponent.getText();
        if (StringUtil.isEmpty(projectRootPath)) {
            return false;
        }

        VirtualFile projectRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(projectRootPath);
        if (projectRoot == null) {
            return false;
        }

        DuneProjectImportBuilder builder = (DuneProjectImportBuilder) getWizardContext().getProjectBuilder();
        if (builder == null) {
            return false;
        }

        builder.setProjectRoot(projectRoot);
        builder.setOpamSettings(new DuneProjectImportBuilder.OpamSettings() {
            @Override public String getOpamLocation() {
                return myOpamConfigurationTab.getOpamLocation().getText();
            }

            @Override public String getOpamSwitch() {
                return myOpamConfigurationTab.getSelectedSwitch();
            }

            @Override public boolean isWsl() {
                return myOpamConfigurationTab.isWsl();
            }

            @Override public String getCygwinBash() {
                return myOpamConfigurationTab.getCygwinBash();
            }
        });

        return true;
    }

    @Override
    public void updateDataModel() {
        String projectRoot = myProjectRootComponent.getText();
        if (!projectRoot.isEmpty()) {
            suggestProjectNameAndPath(null, projectRoot);
        }
    }
}
