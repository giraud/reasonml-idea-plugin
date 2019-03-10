package com.reason;

import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.PersistentOrderRootType;

public class OCamlSourcesOrderRootType extends PersistentOrderRootType {
    public OCamlSourcesOrderRootType() {
        super("OCAML_SOURCES", "sourcePath", null, null);
    }

    public static OrderRootType getInstance() {
        return getOrderRootType(OCamlSourcesOrderRootType.class);
    }

    @Override
    public boolean skipWriteIfEmpty() {
        return true;
    }
}
