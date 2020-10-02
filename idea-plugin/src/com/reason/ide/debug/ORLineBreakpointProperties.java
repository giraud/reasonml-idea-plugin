package com.reason.ide.debug;

import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORLineBreakpointProperties extends XBreakpointProperties<ORLineBreakpointProperties> {
  @Nullable
  @Override
  public ORLineBreakpointProperties getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull ORLineBreakpointProperties state) {}
}
