package com.reason.ide.settings;

import com.intellij.execution.wsl.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.*;
import com.intellij.openapi.roots.libraries.ui.impl.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.ui.*;
import com.intellij.workspaceModel.ide.impl.legacyBridge.library.*;
import com.reason.comp.dune.*;
import com.reason.comp.ocaml.*;
import com.reason.ide.console.*;
import com.reason.ide.library.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;

public class ORSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    @Nls
    private static final String BS_PLATFORM_LOCATION_LABEL = "Choose bs-platform Directory: ";
    @Nls
    private static final String ESY_EXECUTABLE_LABEL = "Choose esy Executable: ";

    private final @NotNull Project myProject;
    private ORSettings mySettings;
    private final List<String[]> myEnv = new ArrayList<>();

    private JPanel myRootPanel;
    private JTabbedPane myTabs;

    private TextFieldWithBrowseButton myOpamLocation;
    private boolean myIsWsl = false;
    private String myCygwinBash;

    // General
    private JTextField f_generalFormatWidthColumns;
    private JCheckBox f_generalIsFormatOnSave;
    private JCheckBox myUseSuperErrorsCheckBox;

    // BuckleScript
    private JCheckBox f_bsIsEnabled;
    private TextFieldWithBrowseButton f_bsPlatformLocation;

    // Opam
    private JComboBox<String> mySwitchSelect;

    // Esy
    private TextFieldWithBrowseButton f_esyExecutable;
    private JLabel myDetectionLabel;
    private JTable myOpamLibraries;

    public ORSettingsConfigurable(@NotNull Project project) {
        myProject = project;
    }

    @NotNull
    @Override
    public String getId() {
        return getHelpTopic();
    }

    @NotNull
    @Nls
    @Override
    public String getDisplayName() {
        return "OCaml(Reason) / Rescript";
    }

    @NotNull
    @Override
    public String getHelpTopic() {
        return "settings.reason";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettings = myProject.getService(ORSettings.class);
        createGeneralTab();
        createBsTab();
        createOpamTab();
        createEsyTab();

        mySwitchSelect.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                String version = (String) itemEvent.getItem();
                clearEnv();
                listLibraries(version);
            }
        });
        myOpamLibraries.setBorder(BorderFactory.createLineBorder(JBUI.CurrentTheme.DefaultTabs.borderColor()));
        listLibraries(mySettings.getSwitchName());

        return myRootPanel;
    }

    @Override
    public void apply() {
        // General
        mySettings.setFormatOnSaveEnabled(f_generalIsFormatOnSave.isSelected());
        mySettings.setFormatColumnWidth(sanitizeInput(f_generalFormatWidthColumns));
        mySettings.setUseSuperErrors(myUseSuperErrorsCheckBox.isSelected());
        // BuckleScript
        mySettings.setBsEnabled(f_bsIsEnabled.isSelected());
        mySettings.setBsPlatformLocation(sanitizeInput(f_bsPlatformLocation));
        // Opam
        mySettings.setOpamLocation(sanitizeInput(myOpamLocation));
        mySettings.setCygwinBash(myCygwinBash);
        mySettings.setIsWsl(myIsWsl);
        mySettings.setSwitchName((String) mySwitchSelect.getSelectedItem());
        // Esy
        mySettings.setEsyExecutable(sanitizeInput(f_esyExecutable));

        // Create external library based on the selected opam switch
        createExternalLibraryDependency(mySettings);
        // Compute env
        OpamEnv opamEnv = myProject.getService(OpamEnv.class);
        opamEnv.computeEnv(mySettings.getOpamLocation(), mySettings.getSwitchName(), mySettings.getCygwinBash(), null);
        // Display compiler info in console (if any)
        myProject.getService(ORToolWindowManager.class).showShowToolWindows();
    }

    private void createExternalLibraryDependency(@NotNull ORSettings settings) {
        Project project = settings.getProject();
        if (settings.getSwitchName().isEmpty()) {
            return;
        }

        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        LibraryTable.ModifiableModel projectLibraryTableModel = projectLibraryTable.getModifiableModel();

        String libraryName = "switch:" + settings.getSwitchName();

        // Remove existing lib
        Library oldLibrary = projectLibraryTableModel.getLibraryByName(libraryName);
        VirtualFile opamRootCandidate = VirtualFileManager.getInstance().findFileByNioPath(Path.of(settings.getOpamLocation(), settings.getSwitchName()));
        if (opamRootCandidate != null && opamRootCandidate.exists() && opamRootCandidate.isValid()) {
            Library library = oldLibrary == null ? projectLibraryTableModel.createLibrary(libraryName, OclLibraryKind.INSTANCE) : null;
            Library.ModifiableModel libraryModel = library == null ? null : library.getModifiableModel();

            if (libraryModel != null) {
                OclLibraryType libraryType = (OclLibraryType) LibraryType.findByKind(OclLibraryKind.INSTANCE);
                LibraryRootsComponentDescriptor rootsComponentDescriptor = libraryType.createLibraryRootsComponentDescriptor();
                List<OrderRoot> orderRoots = RootDetectionUtil.detectRoots(Collections.singleton(opamRootCandidate), myRootPanel, project, rootsComponentDescriptor);
                for (OrderRoot orderRoot : orderRoots) {
                    libraryModel.addRoot(orderRoot.getFile(), orderRoot.getType());
                }
            }

            ApplicationManager.getApplication().invokeAndWait(() -> WriteAction.run(() -> {
                if (libraryModel != null) {
                    libraryModel.commit();
                    projectLibraryTableModel.commit();
                }

                // Find module that contains dune config root file
                Map<Module, VirtualFile> duneContentRoots = Platform.findContentRootsFor(project, DunePlatform.DUNE_PROJECT_FILENAME);
                for (Module module : duneContentRoots.keySet()) {
                    ModuleRootModificationUtil.updateModel(module, moduleModel -> {
                        // Remove all libraries entries that are of type Ocaml
                        moduleModel.orderEntries().forEach(entry -> {
                            Library entryLibrary = (entry instanceof LibraryOrderEntry) ? ((LibraryOrderEntry) entry).getLibrary() : null;
                            PersistentLibraryKind<?> entryLibraryKind = (entryLibrary instanceof LibraryBridge) ? ((LibraryBridge) entryLibrary).getKind() : null;
                            if (entryLibraryKind instanceof OclLibraryKind) {
                                moduleModel.removeOrderEntry(entry);
                            }

                            return true;
                        });
                        // Add the new lib as order entry
                        moduleModel.addLibraryEntry(library == null ? oldLibrary : library);
                    });
                }
            }));
        }
    }


    private void listLibraries(@NotNull String version) {
        myProject.getService(OpamProcess.class)
                .list(myOpamLocation.getText(), version, myCygwinBash, libs -> {
                    myEnv.clear();
                    if (libs != null) {
                        myEnv.addAll(libs);
                    }
                    myOpamLibraries.setModel(createDataModel());
                });
    }

    @NotNull
    private AbstractTableModel createDataModel() {
        return new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return myEnv.size();
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public @NotNull Object getValueAt(int rowIndex, int columnIndex) {
                String[] columns = myEnv.get(rowIndex);
                return columns.length <= columnIndex ? "" : columns[columnIndex];
            }
        };
    }

    private void clearEnv() {
        myEnv.clear();
        myOpamLibraries.setModel(createDataModel());
    }

    @Override
    public boolean isModified() {
        // General
        boolean isFormatOnSaveModified =
                f_generalIsFormatOnSave.isSelected() != mySettings.isFormatOnSaveEnabled();
        boolean isFormatWidthColumnsModified =
                !f_generalFormatWidthColumns.getText().equals(mySettings.getFormatColumnWidth());
        boolean isUseSuperErrorModified = myUseSuperErrorsCheckBox.isSelected() != mySettings.isUseSuperErrors();
        // Bs
        boolean isBsEnabledModified = f_bsIsEnabled.isSelected() != mySettings.isBsEnabled();
        boolean isBsPlatformLocationModified =
                !f_bsPlatformLocation.getText().equals(mySettings.getBsPlatformLocation());
        // Opam
        boolean isOpamLocationModified =
                !myOpamLocation.getText().equals(mySettings.getOpamLocation());
        boolean isOpamSwitchModified = !mySettings.getSwitchName().equals(mySwitchSelect.getSelectedItem());
        // Esy
        boolean isEsyExecutableModified =
                !f_esyExecutable.getText().equals(mySettings.getEsyExecutable());

        return isFormatOnSaveModified || isFormatWidthColumnsModified || isUseSuperErrorModified
                || isBsEnabledModified || isBsPlatformLocationModified || isOpamLocationModified
                || isOpamSwitchModified || isEsyExecutableModified;
    }

    @Override
    public void reset() {
        // General
        f_generalIsFormatOnSave.setSelected(mySettings.isFormatOnSaveEnabled());
        f_generalFormatWidthColumns.setText(mySettings.getFormatColumnWidth());
        myUseSuperErrorsCheckBox.setSelected(mySettings.isUseSuperErrors());
        // BuckleScript
        f_bsIsEnabled.setSelected(mySettings.isBsEnabled());
        f_bsPlatformLocation.setText(mySettings.getBsPlatformLocation());
        // Opam
        myOpamLocation.setText(mySettings.getOpamLocation());
        myCygwinBash = mySettings.getCygwinBash();
        myIsWsl = mySettings.isWsl();
        // Esy
        f_esyExecutable.setText(mySettings.getEsyExecutable());

        setDetectionText();
        createSwitch(mySettings.getOpamLocation());
    }

    private void createGeneralTab() {
    }

    private void setDetectionText() {
        if (myCygwinBash != null) {
            myDetectionLabel.setText("Cygwin detected");
        } else if (myIsWsl) {
            myDetectionLabel.setText("WSL detected");
        } else {
            myDetectionLabel.setText("");
        }
    }

    private void createBsTab() {
        Project project = mySettings.getProject();
        f_bsPlatformLocation.addBrowseFolderListener(BS_PLATFORM_LOCATION_LABEL, null, project,
                FileChooserDescriptorFactory.createSingleFolderDescriptor());
    }

    private void detectSwitchSystem(@NotNull VirtualFile dir) {
        myIsWsl = dir.getPath().replace("/", "\\").startsWith(WSLDistribution.UNC_PREFIX);
        myCygwinBash = null;
        if (!myIsWsl && Platform.isWindows()) { // cygwin
            VirtualFile binDir = findBinary(dir);
            if (binDir != null && binDir.isValid()) {
                VirtualFile opam = binDir.findChild("bash.exe");
                if (opam != null && opam.isValid()) {
                    myCygwinBash = opam.getPath();
                }
            }
        }
    }

    private void createOpamTab() {
        Project project = mySettings.getProject();
        TextBrowseFolderListener browseListener = new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project) {
            @Override
            protected void onFileChosen(@NotNull VirtualFile chosenDir) {
                super.onFileChosen(chosenDir);

                detectSwitchSystem(chosenDir);
                setDetectionText();
                createSwitch(chosenDir.getPath());
            }

        };

        final String[] previousOpamLocation = new String[1];

        FocusListener focusListener = new FocusListener() {
            @Override public void focusGained(FocusEvent e) {
                previousOpamLocation[0] = myOpamLocation.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                String path = myOpamLocation.getText();
                String oldPath = previousOpamLocation[0];
                if (!path.equals(oldPath)) {
                    VirtualFile chosenDir = VirtualFileManager.getInstance().findFileByNioPath(Path.of(path));
                    if (chosenDir == null) {
                        createSwitch("");
                        clearEnv();
                    } else {
                        detectSwitchSystem(chosenDir);
                        setDetectionText();
                        createSwitch(chosenDir.getPath());
                    }
                }
            }
        };

        myOpamLocation.getTextField().addFocusListener(focusListener);
        myOpamLocation.addBrowseFolderListener(browseListener);
    }

    private VirtualFile findBinary(@Nullable VirtualFile dir) {
        if (dir == null) {
            return null;
        }

        VirtualFile child = dir.findChild("bin");
        if (child != null) {
            return child;
        }

        return findBinary(dir.getParent());
    }

    private void createSwitch(@NotNull String opamLocation) {
        OpamProcess opamProcess = new OpamProcess(myProject);
        opamProcess.listSwitch(opamLocation, myCygwinBash, opamSwitches -> {
            boolean switchEnabled = opamSwitches != null && !opamSwitches.isEmpty();
            mySwitchSelect.removeAllItems();
            mySwitchSelect.setEnabled(switchEnabled);
            if (switchEnabled) {
                boolean useOpamSelection = mySettings.getSwitchName().isEmpty();
                //System.out.println("Add: [" + Joiner.join(", ", opamSwitches) + "]");
                for (OpamProcess.OpamSwitch opamSwitch : opamSwitches) {
                    mySwitchSelect.addItem(opamSwitch.name);
                    if (opamSwitch.isSelected && useOpamSelection) {
                        mySwitchSelect.setSelectedIndex(mySwitchSelect.getItemCount() - 1);
                    }
                }
                if (!useOpamSelection) {
                    mySwitchSelect.setSelectedItem(mySettings.getSwitchName());
                }
            } else {
                clearEnv();
            }
        });
    }

    private void createEsyTab() {
        Project project = mySettings.getProject();
        f_esyExecutable.addBrowseFolderListener(ESY_EXECUTABLE_LABEL, null, project,
                FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
    }

    private static @NotNull String sanitizeInput(@NotNull JTextField textField) {
        return sanitizeInput(textField.getText());
    }

    private static @NotNull String sanitizeInput(@NotNull TextFieldWithBrowseButton textFieldWithBrowseButton) {
        return sanitizeInput(textFieldWithBrowseButton.getText());
    }

    private static @NotNull String sanitizeInput(@NotNull String input) {
        return input.trim();
    }
}
