package com.reason.ide.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(
        name = "ReasonSettings",
        storages = {@Storage("reason.xml")}
)
public class ReasonSettings implements PersistentStateComponent<ReasonSettings> {
    @SuppressWarnings("WeakerAccess")
    public String location = "";
    @SuppressWarnings("WeakerAccess")
    public boolean refmtOnSave = true;
    @SuppressWarnings("WeakerAccess")
    public String refmtWidth = "120";

    public static ReasonSettings getInstance() {
        return ServiceManager.getService(ReasonSettings.class);
    }

    @NotNull
    @Override
    public ReasonSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ReasonSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
