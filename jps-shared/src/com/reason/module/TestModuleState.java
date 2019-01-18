package com.reason.module;

import com.intellij.openapi.components.BaseState;

public class TestModuleState extends BaseState {
    private String level = "x";

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
