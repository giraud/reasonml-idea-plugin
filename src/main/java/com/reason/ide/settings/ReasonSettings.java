package com.reason.ide.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ReasonOptions",
        storages = {
                @Storage(file = StoragePathMacros.APP_CONFIG + "/reason.xml")}
)
public class ReasonSettings implements PersistentStateComponent<ReasonOptions> {
    private ReasonOptions m_state = new ReasonOptions();

    @Nullable
    @Override
    public ReasonOptions getState() {
        return m_state;
    }

    @Override
    public void loadState(ReasonOptions state) {
        m_state = state;
    }

    public String getRefmtWidth() {
        return m_state.m_refmtWidth;
    }

    public void setRefmtWidth(String value) {
        m_state.m_refmtWidth = value;
    }
}
