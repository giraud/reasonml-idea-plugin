package com.reason.comp.bs;

import com.intellij.framework.detection.FileContentPattern;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.FileContentImpl;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class BsConfigJson {

  private BsConfigJson() {}

  public static boolean isBsConfigJson(@NotNull VirtualFile virtualFile) {
    if (virtualFile.getFileType() instanceof JsonFileType) {
      try {
        FileContent fileContent = FileContentImpl.createByFile(virtualFile);
        return createFilePattern().accepts(fileContent);
      } catch (IOException e) {
        return false;
      }
    }
    return false;
  }

  private static ElementPattern<FileContent> createFilePattern() {
    return FileContentPattern.fileContent().withName("bsconfig.json");
  }
}
