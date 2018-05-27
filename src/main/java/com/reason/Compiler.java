package com.reason;

import com.intellij.openapi.fileTypes.FileType;

public interface Compiler {

    void refresh();

    void run(FileType fileType);

}
