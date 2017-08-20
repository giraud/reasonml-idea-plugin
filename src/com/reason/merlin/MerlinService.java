package com.reason.merlin;

import com.reason.merlin.types.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MerlinService {
    boolean isRunning();

    List<MerlinError> errors(String filename);

    @Nullable
    MerlinVersion version();

    @Nullable
    MerlinVersion selectVersion(int version);

    void sync(String filename, String buffer);

    @Nullable
    Object dump(String filename, DumpFlag flag);

    List<MerlinToken> dumpTokens(String filename);

    List<String> paths(String filename, Path path);

    List<String> listExtensions(String filename);

    void enableExtensions(String filename, List<String> extensions);

    @Nullable
    Object projectGet();

    List<MerlinType> findType(String filename, MerlinPosition position);

    void outline(String filename);

    MerlinCompletion completions(String filename, String prefix, MerlinPosition position);
}
