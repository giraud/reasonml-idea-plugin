package com.reason.build;

import com.intellij.openapi.fileTypes.FileType;

public interface Compiler {

    void refresh();

    void run(FileType fileType);

}
