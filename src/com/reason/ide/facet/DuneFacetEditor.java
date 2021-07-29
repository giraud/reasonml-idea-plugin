package com.reason.ide.facet;

import com.intellij.facet.ui.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.roots.ui.configuration.projectRoot.*;
import com.intellij.openapi.util.*;
import com.intellij.util.ui.*;
import com.reason.comp.dune.*;
import com.reason.ide.console.*;
import jpsplugin.com.reason.sdk.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;

import static com.intellij.openapi.roots.ui.configuration.JdkComboBox.*;

class DuneFacetEditor extends FacetEditorTab {
    private final @NotNull DuneFacetConfiguration m_configuration;
    private final @NotNull FacetEditorContext m_editorContext;

    private JPanel c_root;
    private JCheckBox c_inheritProjectSDKCheck;
    private @Nullable JdkComboBox c_sdkSelect;
    private JTable c_table;

    private List<String[]> m_env;

    DuneFacetEditor(@NotNull FacetEditorContext editorContext, @NotNull DuneFacetConfiguration configuration) {
        m_editorContext = editorContext;
        m_configuration = configuration;
    }

    @NotNull
    @Nls
    @Override
    public String getDisplayName() {
        return "Dune";
    }

    private void createUIComponents() {
        Module m_module = m_editorContext.getModule();
        ProjectSdksModel model = new ProjectSdksModel();
        Project project = m_module.getProject();
        Condition<SdkTypeId> filter = sdkTypeId -> OCamlSdkType.ID.equals(sdkTypeId.getName());

        model.reset(project);
        c_sdkSelect = new JdkComboBox(project, model, filter, getSdkFilter(filter), filter, null);
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        DuneFacetConfiguration state = m_configuration.getState();
        assert state != null;

        Sdk selectedOdk = null;

        if (c_sdkSelect != null) {
            c_inheritProjectSDKCheck.addItemListener(
                    itemEvent -> c_sdkSelect.setEnabled(itemEvent.getStateChange() == ItemEvent.DESELECTED));
            //c_inheritProjectSDKCheck.setSelected(state.inheritProjectSdk);

            //if (state.inheritProjectSdk) {
            Module module = m_editorContext.getModule();
            Sdk odk = OCamlSdkType.getSDK(module.getProject());
            if (odk != null) {
                c_sdkSelect.setSelectedJdk(odk);
                selectedOdk = odk;
            }
            /*
            } else {
                int itemCount = c_sdkSelect.getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    JdkComboBoxItem comboSdk = c_sdkSelect.getItemAt(i);
                    String name = comboSdk.getSdkName();
                    if (name != null && name.equals(m_configuration.sdkName)) {
                        c_sdkSelect.setSelectedIndex(i);
                        selectedOdk = comboSdk.getJdk();
                        break;
                    }
                }
            }
            */
        }

        c_sdkSelect.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                Object item = itemEvent.getItem();
                if (item instanceof JdkComboBoxItem) {
                    Sdk odk = ((JdkComboBoxItem) item).getJdk();
                    clearEnv();
                    listLibraries(odk);
                }
            }
        });

        m_env = new ArrayList<>();
        c_table.setBorder(BorderFactory.createLineBorder(JBUI.CurrentTheme.DefaultTabs.borderColor()));

        listLibraries(selectedOdk);

        return c_root;
    }

    private void clearEnv() {
        m_env.clear();
        c_table.setModel(createDataModel());
    }

    private void listLibraries(@Nullable Sdk odk) {
        if (odk != null) {
            ServiceManager.getService(m_editorContext.getProject(), OpamProcess.class)
                    .list(odk, libs -> {
                        m_env.clear();
                        if (libs != null) {
                            m_env.addAll(libs);
                        }
                        c_table.setModel(createDataModel());
                    });
        }
    }

    @NotNull
    private AbstractTableModel createDataModel() {
        return new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return m_env.size();
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                String[] columns = m_env.get(rowIndex);
                return columns.length <= columnIndex ? "" : columns[columnIndex];
            }
        };
    }

    @Override
    public boolean isModified() {
        Sdk odk = c_sdkSelect == null ? null : c_sdkSelect.getSelectedJdk();
        String odkName = odk == null ? "" : odk.getName();
        String confOdkName = m_configuration.sdkName == null ? "" : m_configuration.sdkName;
        return m_configuration.inheritProjectSdk != c_inheritProjectSDKCheck.isSelected()
                || !odkName.equals(confOdkName);
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();
        //m_configuration.inheritProjectSdk = c_inheritProjectSDKCheck.isSelected();
        Sdk odk = c_sdkSelect == null ? null : c_sdkSelect.getSelectedJdk();
        m_configuration.sdkName = odk == null ? null : odk.getName();

        // @TODO see https://github.com/reasonml-editor/reasonml-idea-plugin/issues/243
        // show tool window if dune is now configured
        // should use a listener instead as this doesn't trigger when the facet is removed
        ORToolWindowManager toolWindowManager = ServiceManager.getService(m_editorContext.getProject(), ORToolWindowManager.class);
        ApplicationManager.getApplication().invokeLater(toolWindowManager::showHideToolWindows);
    }
}
