package com.reason.bs.compiler;

import com.intellij.execution.process.ProcessListener;
import org.jetbrains.annotations.NotNull;

public interface RawProcessListener extends ProcessListener {
    void onRawTextAvailable(@NotNull String replace);
}
