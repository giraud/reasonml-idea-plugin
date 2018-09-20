package com.reason.lang.core.psi.type;

import com.intellij.lang.Language;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

public final class ORTypesUtil {

    public static MlTypes getInstance(@NotNull Language language) {
        return language instanceof RmlLanguage || language instanceof OclLanguage ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
    }

}
