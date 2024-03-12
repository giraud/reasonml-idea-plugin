package com.reason.ide.settings;

import com.intellij.openapi.application.*;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.impl.wsl.*;
import com.intellij.util.ui.*;
import com.reason.comp.ocaml.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;

public class OpamConfigurationTab {
    private static final String[] EMPTY_COLUMNS = {};

    private JPanel myRootPanel;
    private TextFieldWithBrowseButton myOpamLocation;
    private JLabel myDetectionLabel;
    private JComboBox<String> mySwitchSelect;
    private JTable myOpamLibraries;


    private boolean myIsWsl = false;
    private String myCygwinBash;
    private final List<String[]> myEnv = new ArrayList<>();

    public void createComponent(@Nullable Project project, @NotNull String switchName) {
        TextBrowseFolderListener browseListener = new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project) {
            @Override
            protected void onFileChosen(@NotNull VirtualFile chosenDir) {
                super.onFileChosen(chosenDir);

                detectSwitchSystem(chosenDir);
                setDetectionText();
                createSwitch(chosenDir.getPath(), switchName);
            }

        };

        FocusListener focusListener = getFocusListener(switchName);

        myOpamLocation.getTextField().addFocusListener(focusListener);
        myOpamLocation.addBrowseFolderListener(browseListener);

        mySwitchSelect.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                String version = (String) itemEvent.getItem();
                clearEnv();
                listLibraries(version);
            }
        });

        myOpamLibraries.setBorder(BorderFactory.createLineBorder(JBUI.CurrentTheme.DefaultTabs.borderColor()));

        listLibraries(switchName);
    }

    private @NotNull FocusListener getFocusListener(@NotNull String switchName) {
        final String[] previousOpamLocation = new String[1];

        return new FocusListener() {
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
                        createSwitch("", switchName);
                        clearEnv();
                    } else {
                        detectSwitchSystem(chosenDir);
                        setDetectionText();
                        createSwitch(chosenDir.getPath(), switchName);
                    }
                }
            }
        };
    }

    private void detectSwitchSystem(@NotNull VirtualFile dir) {
        myIsWsl = dir.getPath().replace("/", "\\").startsWith(WslConstants.UNC_PREFIX);
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

    void setDetectionText() {
        if (myCygwinBash != null) {
            myDetectionLabel.setText("Cygwin detected");
        } else if (myIsWsl) {
            myDetectionLabel.setText("WSL detected");
        } else {
            myDetectionLabel.setText("");
        }
    }

    void createSwitch(@NotNull String opamLocation, @NotNull String switchName) {
        ApplicationManager.getApplication()
                .getService(OpamProcess.class)
                .listSwitch(opamLocation, myCygwinBash, opamSwitches -> {
                    boolean switchEnabled = opamSwitches != null && !opamSwitches.isEmpty();
                    mySwitchSelect.removeAllItems();
                    mySwitchSelect.setEnabled(switchEnabled);
                    if (switchEnabled) {
                        boolean useOpamSelection = switchName.isEmpty();
                        //System.out.println("Add: [" + Joiner.join(", ", opamSwitches) + "]");
                        for (OpamProcess.OpamSwitch opamSwitch : opamSwitches) {
                            mySwitchSelect.addItem(opamSwitch.name());
                            if (opamSwitch.isSelected() && useOpamSelection) {
                                mySwitchSelect.setSelectedIndex(mySwitchSelect.getItemCount() - 1);
                            }
                        }
                        if (!useOpamSelection) {
                            mySwitchSelect.setSelectedItem(switchName);
                        }
                    } else {
                        clearEnv();
                    }
                });
    }

    private void listLibraries(@NotNull String version) {
        ApplicationManager.getApplication().getService(OpamProcess.class)
                .list(myOpamLocation.getText(), version, myCygwinBash, libs -> {
                    myEnv.clear();
                    if (libs != null) {
                        myEnv.addAll(libs);
                    }
                    myOpamLibraries.setModel(createDataModel());
                });
    }

    void clearEnv() {
        myEnv.clear();
        myOpamLibraries.setModel(createDataModel());
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
                String[] columns = rowIndex < getRowCount() ? myEnv.get(rowIndex) : EMPTY_COLUMNS;
                return columns.length <= columnIndex ? "" : columns[columnIndex];
            }
        };
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

    public TextFieldWithBrowseButton getOpamLocation() {
        return myOpamLocation;
    }

    public void setOpamLocation(@NotNull String opamLocation) {
        myOpamLocation.setText(opamLocation);
    }

    public @Nullable String getSelectedSwitch() {
        return (String) mySwitchSelect.getSelectedItem();
    }

    public boolean isWsl() {
        return myIsWsl;
    }

    public @Nullable String getCygwinBash() {
        return myCygwinBash;
    }

    public boolean isOpamLocationModified(String opamLocation) {
        return !myOpamLocation.getText().equals(opamLocation);
    }

    public boolean isOpamSwitchModified(@NotNull String switchName) {
        return !switchName.equals(mySwitchSelect.getSelectedItem());
    }
}
