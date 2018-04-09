import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiPatternMatch;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlParserDefinition;

import java.util.Collection;

public class SwitchParsingReTest extends BaseParsingTestCase {
    public SwitchParsingReTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testPattern() {
        PsiFileModuleImpl psiFileModule = parseCode("switch (x) { | Some(x) => x; (); | None => () };");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFileModule, PsiSwitch.class));
        assertNotNull(switch_);

        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        assertEquals(2, patterns.size());
    }

}
