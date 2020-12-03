package com.reason.ide.format;

import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

public class ReformatUtil {

  private ReformatUtil() {
  }

  @Nullable
  public static String getFormat(@Nullable PsiFile file) {
    String format = null;

    if (file != null) {
      FileType fileType = file.getFileType();
      boolean isInterface = FileHelper.isInterface(fileType);

      if (FileHelper.isOCaml(fileType)) {
        format = isInterface ? "mli" : "ml";
      } else if (FileHelper.isReason(fileType)) {
        format = isInterface ? "rei" : "re";
      } else if (FileHelper.isRescript(fileType)) {
        format = isInterface ? "resi" : "res";
      }
    }

    return format;
  }
}
