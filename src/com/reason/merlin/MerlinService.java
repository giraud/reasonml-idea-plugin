package com.reason.merlin;

import java.util.List;

public interface MerlinService {
    List<MerlinError> errors();

    String version();

    Object dump(DumpFlag flag);

    List<MerlinToken> dumpTokens();

    List<String> paths(Path path);

    List<String> listExtensions();
}
