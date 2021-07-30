package jpsplugin.com.reason;

import com.intellij.openapi.roots.*;
import org.jetbrains.annotations.*;

public class OCamlSourcesOrderRootType extends PersistentOrderRootType {
    private static OCamlSourcesOrderRootType INSTANCE;

    public OCamlSourcesOrderRootType() {
        super("OCAML_SOURCES", "OCamlSourcePath", null, null);
    }

    public static @NotNull OrderRootType getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OCamlSourcesOrderRootType();
        }
        return INSTANCE;
    }

    @Override
    public boolean skipWriteIfEmpty() {
        return true;
    }
}
