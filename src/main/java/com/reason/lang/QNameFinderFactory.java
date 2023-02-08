package com.reason.lang;

import com.intellij.lang.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

public class QNameFinderFactory {
    private QNameFinderFactory() {
    }

    @NotNull
    public static QNameFinder getQNameFinder(@NotNull Language language) {
        return language == OclLanguage.INSTANCE
                ? OclQNameFinder.INSTANCE
                : language == ResLanguage.INSTANCE ? ResQNameFinder.INSTANCE : RmlQNameFinder.INSTANCE;
    }
}
