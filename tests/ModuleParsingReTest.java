import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.reason.RmlParserDefinition;

import java.io.IOException;
import java.util.Collection;

public class ModuleParsingReTest extends BaseParsingTestCase {
    public ModuleParsingReTest() {
        super("module", "re", new RmlParserDefinition());
    }

    public void testModuleDef() {
        PsiModule psiModule = doRmlTest();
        Collection<PsiModule> modules = psiModule.getModules();

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testEmpty() throws IOException {
        Collection<PsiModule> modules = parseCode("module M = {};").getModules();

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testAlias() throws IOException {
        PsiModule module = first(parseCode("module M = Y;").getModules());

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

}
