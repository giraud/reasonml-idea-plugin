package jpsplugin.com.reason;

import com.intellij.openapi.roots.*;
import org.jetbrains.annotations.*;

public class OclSourcesOrderRootType extends PersistentOrderRootType {
    public OclSourcesOrderRootType() {
        super("OCAML_SOURCES", null, null, null);
    }

    @NotNull
    public static OclSourcesOrderRootType getInstance() {
        return getOrderRootType(OclSourcesOrderRootType.class);
    }
}
