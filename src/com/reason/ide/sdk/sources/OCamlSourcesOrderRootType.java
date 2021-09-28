package com.reason.ide.sdk.sources;

import com.intellij.openapi.roots.*;
import org.jetbrains.annotations.*;

/**
 * Every root is SET as OCAML_SOURCES
 *
 * @see OCamlRootsDetector
 */
public class OCamlSourcesOrderRootType extends PersistentOrderRootType {

    protected OCamlSourcesOrderRootType() {
        super("OCAML_SOURCES", "OCamlSourcePath", null, null);
    }

    @NotNull
    public static OCamlSourcesOrderRootType getInstance() {
        return getOrderRootType(OCamlSourcesOrderRootType.class);
    }

}
