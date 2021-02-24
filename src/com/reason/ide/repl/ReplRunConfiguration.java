package com.reason.ide.repl;

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
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReplRunConfiguration extends RunConfigurationBase<ReplRunConfiguration> {
    private @Nullable Sdk m_sdk;

    ReplRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
/*
        ProjectSdksModel model = new ProjectSdksModel();
        model.reset(getProject());
        for (Sdk sdk : model.getSdks()) {
            if ("OCaml SDK".equals(sdk.getSdkType().getName())) {
                m_sdk = sdk;
                return;
            }
        }
*/
    }

    @Override
    public @NotNull SettingsEditor<ReplRunConfiguration> getConfigurationEditor() {
        return new ReplRunSettingsEditor(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (m_sdk == null) {
            throw new RuntimeConfigurationException("No SDK selected");
        }
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new ReplGenericState(executionEnvironment);
    }

    @Nullable
    Sdk getSdk() {
        return m_sdk;
    }

    void setSdk(@Nullable Sdk selectedJdk) {
        m_sdk = selectedJdk;
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        element.addContent(new Element("sdk").setAttribute("name", m_sdk == null ? "" : m_sdk.getName()));
        super.writeExternal(element);
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
        }
    }
}
