package reason.lang;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class RmlElementType extends IElementType {
    RmlElementType(@NotNull @NonNls String debugName) {
        super(debugName, RmlLanguage.INSTANCE);
    }
}
