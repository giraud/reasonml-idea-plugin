import com.intellij.testFramework.ParsingTestCase;
import com.reason.lang.RmlParserDefinition;

public class LetConstantsTest extends ParsingTestCase {
    public LetConstantsTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testLetConstants() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}
