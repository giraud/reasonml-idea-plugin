import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.reason.RmlParserDefinition;

public class LetParsingReTest extends BaseParsingTestCase {
    public LetParsingReTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(parseCode("let x = 1;").getLetExpressions());
        assertEquals("x", let.getName());
    }

    public void testFunction() {
        PsiLet let = first(parseCode("let add = (x,y) => x + y;").getLetExpressions());

        PsiParameters params = first(PsiTreeUtil.findChildrenOfType(let, PsiParameters.class));
        assertEquals(2, params.getArgumentsCount());
        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
    }

}
