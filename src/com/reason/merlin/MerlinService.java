package com.reason.merlin;

import com.reason.merlin.types.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MerlinService {
    boolean isRunning();

    @Nullable
    MerlinVersion selectVersion(int version);

    void sync(String filename, String source);

    List<MerlinError> errors(String filename, String source);

    List<MerlinType> typeExpression(String filename, String source, MerlinPosition position);

    MerlinCompletion completions(String filename, String source, MerlinPosition position, String prefix);
}
