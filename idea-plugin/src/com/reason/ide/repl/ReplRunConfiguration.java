package com.reason.ide.repl;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;

public class ReplRunConfiguration extends RunConfigurationBase<ReplRunConfiguration> {
    @Nullable
    private Sdk m_sdk;
    private boolean m_cygwinEnabled = false;
    private String m_cygwinPath = "";

    ReplRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
        ProjectSdksModel model = new ProjectSdksModel();
        model.reset(getProject());
        for (Sdk sdk : model.getSdks()) {
            if ("OCaml SDK".equals(sdk.getSdkType().getName())) {
                m_sdk = sdk;
                return;
            }
        }
    }

    @NotNull
    @Override
    public SettingsEditor<ReplRunConfiguration> getConfigurationEditor() {
        return new ReplRunSettingsEditor(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (m_sdk == null) {
            throw new RuntimeConfigurationException("No SDK selected");
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new ReplGenericState(executionEnvironment);
    }

    @Nullable Sdk getSdk() {
        return m_sdk;
    }

    void setSdk(@Nullable Sdk selectedJdk) {
        m_sdk = selectedJdk;
    }

    boolean getCygwinSelected() {
        return m_cygwinEnabled;
    }

    void setCygwinSelected(boolean enabled) {
        m_cygwinEnabled = enabled;
    }

    String getCygwinPath() {
        return m_cygwinPath;
    }

    void setCygwinPath(String path) {
        m_cygwinPath = path;
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
        element.addContent(new Element("sdk").
                setAttribute("name", m_sdk == null ? "" : m_sdk.getName()).
                setAttribute("cygwin", Boolean.toString(m_cygwinEnabled)).
                setAttribute("cygwinPath", m_cygwinPath == null ? "" : m_cygwinPath));
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        Element node = element.getChild("sdk");
        if (node != null) {
            String sdkName = node.getAttributeValue("name");
            if (!sdkName.isEmpty()) {
                ProjectSdksModel model = new ProjectSdksModel();
                model.reset(getProject());
                for (Sdk sdk : model.getSdks()) {
                    if (sdkName.equals(sdk.getName())) {
                        m_sdk = sdk;
                        break;
                    }
                }
            }
            m_cygwinEnabled = Boolean.parseBoolean(node.getAttributeValue("cygwin"));
            m_cygwinPath = node.getAttributeValue("cygwinPath");
        }
    }
}
