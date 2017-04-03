package com.reason.merlin;

import com.reason.merlin.types.*;

import java.util.List;

public interface MerlinService {
    boolean isRunning();

    List<MerlinError> errors(String filename);

    MerlinVersion version();

    MerlinVersion selectVersion(int version);

    void sync(String filename, String buffer);

    Object dump(String filename, DumpFlag flag);

    List<MerlinToken> dumpTokens(String filename);

    List<String> paths(String filename, Path path);

    List<String> listExtensions(String filename);

    void enableExtensions(String filename, List<String> extensions);

    Object projectGet();

    List<MerlinType> findType(String filename, MerlinPosition position);
}
