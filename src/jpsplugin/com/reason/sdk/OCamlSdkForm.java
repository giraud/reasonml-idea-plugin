package jpsplugin.com.reason.sdk;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.reason.ide.sdk.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor;

public class OCamlSdkForm {
    private final Log LOG = Log.create("sdk");

    private @Nullable Sdk m_odk;
    private @Nullable OCamlSdkAdditionalData m_data;
    private @Nullable String m_sdkVersion;

    private JPanel c_rootPanel;
    private JButton c_download;
    private JTextField c_forceValue;
    private JLabel c_version;
    private TextFieldWithBrowseButton c_sdkHome;
    private JCheckBox c_cygwin;
    private JCheckBox c_forceVersion;
    private TextFieldWithBrowseButton c_cygwinBash;

    public void createUIComponents(@NotNull Sdk sdk) {
        m_odk = sdk;
        SdkTypeId sdkType = sdk.getSdkType();
        if (sdkType instanceof OCamlSdkType) {
            m_sdkVersion = sdk.getVersionString();
            m_data = (OCamlSdkAdditionalData) sdk.getSdkAdditionalData();
            if (m_data != null) {
                c_cygwin.setSelected(m_data.isCygwin());
                c_cygwin.addItemListener(itemEvent -> c_cygwinBash.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED));

                c_cygwinBash.setEnabled(m_data.isCygwin());
                c_cygwinBash.setText(m_data.getCygwinBash());
                //noinspection DialogTitleCapitalization
                c_cygwinBash.addBrowseFolderListener("Choose Cygwin bash.exe:", null, null, createSingleFileDescriptor("exe"));

                c_version.setText("Current version is: " + m_data);

                c_forceVersion.setSelected(m_data.isForced());
                c_forceVersion.addItemListener(
                        itemEvent -> c_forceValue.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED));

                c_forceValue.setText(m_data.toString());
                c_forceValue.setEnabled(m_data.isForced());

                c_sdkHome.addBrowseFolderListener("Choose Sdk Home Directory: ", null, null, FileChooserDescriptorFactory.createSingleFolderDescriptor());

                c_download.addMouseListener(
                        new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent mouseEvent) {
                                OCamlSdkAdditionalData odkData =
                                        (OCamlSdkAdditionalData) sdk.getSdkAdditionalData();
                                if (odkData != null) {
                                    // Download SDK from distribution site
                                    LOG.debug("Download SDK", m_data.toString());
                                    VirtualFileSystem fileSystem = LocalFileSystem.getInstance();
                                    VirtualFile sdkHome = fileSystem.findFileByPath(c_sdkHome.getText().trim());
                                    if (sdkHome != null) {
                                        Task.WithResult<String, RuntimeException> download =
                                                new Task.WithResult(null, "Download SDK", false) {
                                                    @Override
                                                    protected @NotNull String compute(@NotNull ProgressIndicator indicator) throws RuntimeException {
                                                        new SdkDownloader(odkData.getMajor(), odkData.getMinor(), odkData.getPatch(), sdkHome).run(null, indicator);
                                                        return sdkHome.getPath();
                                                    }
                                                };
                                        String path = ProgressManager.getInstance().run(download);
                                        Notifications.Bus.notify(
                                                new ORNotification("SDK", "SDK " + odkData + " downloaded to " + path, NotificationType.INFORMATION));
                                    }
                                }
                            }
                        });
            }
        }
    }

    public JComponent getComponent() {
        return c_rootPanel;
    }

    public boolean isModified() {
        if (m_data != null) {
            return m_data.isCygwin() != c_cygwin.isSelected()
                    || !c_cygwinBash.getText().trim().equals(m_data.getCygwinBash())
                    || m_data.isForced() != c_forceVersion.isSelected()
                    || (c_forceVersion.isSelected()
                    && !c_forceValue.getText().trim().equals(m_data.toString()));
        }

        return false;
    }

    public void apply() {
        if (m_data != null) {
            m_data.setCygwin(c_cygwin.isSelected());
            m_data.setCygwinBash(c_cygwinBash.getText().trim());
            m_data.setForced(c_forceVersion.isSelected());
            if (c_forceVersion.isSelected()) {
                m_data.setVersionFromHome(c_forceValue.getText().trim());
            } else {
                m_data.setVersionFromHome(m_sdkVersion == null ? "?" : m_sdkVersion);
            }
            c_version.setText("Current version is: " + m_data);

            // OCamlSdkType.reindexSourceRoots(m_odk);
        }
    }
}
