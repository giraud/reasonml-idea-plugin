package com.reason.comp.bs;

import com.intellij.execution.process.ProcessListener;
import org.jetbrains.annotations.NotNull;

public interface RawProcessListener extends ProcessListener {
  void onRawTextAvailable(@NotNull String replace);
}
