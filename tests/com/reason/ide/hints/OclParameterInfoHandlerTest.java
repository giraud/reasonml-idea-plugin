package com.reason.ide.hints;

import com.intellij.psi.*;
import com.intellij.testFramework.utils.parameterInfo.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class OclParameterInfoHandlerTest extends ORBasePlatformTestCase {
    @Test
    public void test_before() {
        configureCode("A.mli", "val add: int -> int -> int");
        configureCode("B.ml", "A.add <caret> 1");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("int -> int -> int", context.text);
        assertEquals(-1, context.currentParam);
    }

    @Test
    public void test_basic() {
        configureCode("A.mli", "val add: int -> int -> int");
        configureCode("B.ml", "A.add 1<caret> 1");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("int -> int -> int", context.text);
        assertEquals(0, context.currentParam);
    }

    @Test
    public void test_eof() {
        configureCode("A.mli", "val add: int -> int -> int");
        configureCode("B.ml", "A.add 1<caret>");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("int -> int -> int", context.text);
        assertEquals(0, context.currentParam);
    }

    @Test
    public void test_whitespace() {
        configureCode("A.mli", "val add: int -> int -> int");
        configureCode("B.ml", "A.add 1 <caret>");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("int -> int -> int", context.text);
        assertEquals(0/*1?*/, context.currentParam);
    }

    @Test
    public void test_intf_impl() {
        configureCode("A.mli", "val add: int -> int -> int");
        configureCode("A.ml", "let add x y = x + y");
        configureCode("B.ml", "let _ = A.add 1<caret>");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("int -> int -> int", context.text);
        assertEquals(0, context.currentParam);
    }

    @SuppressWarnings("ConstantConditions")
    private UIInfoContext getParameterInfoUI() {
        ORParameterInfoHandler handler = new OclParameterInfoHandler();
        MockCreateParameterInfoContext infoContext = new MockCreateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());

        PsiParameters paramsOwner = handler.findElementForParameterInfo(infoContext);
        if (paramsOwner == null) {
            return new UIInfoContext("NULL", -1);
        }
        handler.showParameterInfo(paramsOwner, infoContext);

        MockParameterInfoUIContext<PsiElement> context = new MockParameterInfoUIContext<>(paramsOwner);
        handler.updateUI((RmlParameterInfoHandler.ArgumentsDescription) infoContext.getItemsToShow()[0], context);

        MockUpdateParameterInfoContext updateContext = new MockUpdateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());
        PsiParameters updateParamsOwner = handler.findElementForUpdatingParameterInfo(updateContext);
        updateContext.setParameterOwner(updateParamsOwner);
        handler.updateParameterInfo(updateParamsOwner, updateContext);

        return new UIInfoContext(context.getText(), updateContext.getCurrentParameter());
    }

    static class UIInfoContext {
        String text;
        int currentParam;

        public UIInfoContext(String text, int currentParam) {
            this.text = text;
            this.currentParam = currentParam;
        }
    }
}
