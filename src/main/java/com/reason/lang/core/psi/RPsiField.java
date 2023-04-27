package com.reason.lang.core.psi;

import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public interface RPsiField extends RPsiQualifiedPathElement {
    @Nullable RPsiFieldValue getValue();
}
