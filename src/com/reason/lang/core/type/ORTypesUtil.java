package com.reason.lang.core.type;

import com.intellij.lang.Language;
import com.reason.lang.napkin.NsLanguage;
import com.reason.lang.napkin.NsTypes;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

public final class ORTypesUtil {

  @NotNull
  public static ORTypes getInstance(@NotNull Language language) {
    if (language instanceof OclLanguage) {
      return OclTypes.INSTANCE;
    }
    if (language instanceof NsLanguage) {
      return NsTypes.INSTANCE;
    }
    return RmlTypes.INSTANCE;
  }
}
