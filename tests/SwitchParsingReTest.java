import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlParserDefinition;

public class SwitchParsingReTest extends BaseParsingTestCase {
    public SwitchParsingReTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testPattern() {
        PsiFileModuleImpl psiFileModule = parseCode("switch (x) { | Some(x) => x; (); | None => () };");
        debugPsiAst(psiFileModule);
//        PsiLet let = first(psiFileModule.getLetExpressions());
//
//        PsiParameters params = first(PsiTreeUtil.findChildrenOfType(let, PsiParameters.class));
//        assertEquals(2, params.getArgumentsCount());
//        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
//        assertNotNull(binding);
    }

}
