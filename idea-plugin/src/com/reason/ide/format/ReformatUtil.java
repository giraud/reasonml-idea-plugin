package com.reason.ide.format;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileHelper;
import org.jetbrains.annotations.Nullable;

public class ReformatUtil {

  private ReformatUtil() {}

  @Nullable
  public static String getFormat(@Nullable PsiFile file) {
    String format = null;

    if (file != null) {
      FileType fileType = file.getFileType();
      if (FileHelper.isOCaml(fileType)) {
        format = "ml";
      } else if (FileHelper.isReason(fileType)) {
        format = "re";
      } else if (FileHelper.isRescript(fileType)) {
        format = "res";
      }
    }

    return format;
  }
}
