package com.reason.ide.debug;

import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.Nullable;

public class ORLineBreakpointProperties extends XBreakpointProperties<ORLineBreakpointProperties> {
    @Nullable
    @Override
    public ORLineBreakpointProperties getState() {
        return this;
    }

    @Override
    public void loadState(ORLineBreakpointProperties state) {
    }
}
