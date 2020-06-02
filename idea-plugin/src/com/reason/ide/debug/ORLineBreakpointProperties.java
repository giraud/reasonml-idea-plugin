package com.reason.ide.debug;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;

public class ORLineBreakpointProperties extends XBreakpointProperties<ORLineBreakpointProperties> {
    @Nullable
    @Override
    public ORLineBreakpointProperties getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ORLineBreakpointProperties state) {
    }
}
