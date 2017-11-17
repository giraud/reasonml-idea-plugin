package reason.lang;

import com.intellij.lexer.FlexAdapter;
import com.reason.lang.ReasonMLLexer;

public class RmlLexerAdapter extends FlexAdapter {
    public RmlLexerAdapter() {
        super(new ReasonMLLexer(null));
    }
}
