package com.reason.ide.hints;

import com.intellij.psi.*;
import com.intellij.testFramework.utils.parameterInfo.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class ResParameterInfoHandlerTest extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.resi", "let add : (int, int) => int");
        configureCode("B.res", "A.add(<caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("(int, int) => int", context.text);
        assertEquals(0, context.currentParam);
    }

    @Test
    public void test_intf_impl() {
        configureCode("A.resi", "let add : (int, int) => int");
        configureCode("A.res", "let add = (x, y) => x + y");
        configureCode("B.res", "A.add(<caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("(int, int) => int", context.text);
        assertEquals(0, context.currentParam);
    }

    @Test
    public void test_empty() {
        configureCode("A.resi", "let fn : unit => string");
        configureCode("B.res", "A.fn(<caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("unit => string", context.text);
        assertEquals(0, context.currentParam);
    }

    @Test
    public void test_item() {
        configureCode("A.resi", "let add : (int, int) => int");
        configureCode("B.res", "A.add(1, <caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("(int, int) => int", context.text);
        assertEquals(1, context.currentParam);
    }

    @SuppressWarnings("ConstantConditions")
    private UIInfoContext getParameterInfoUI() {
        ResParameterInfoHandler handler = new ResParameterInfoHandler();
        MockCreateParameterInfoContext infoContext = new MockCreateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());

        RPsiParameters paramsOwner = handler.findElementForParameterInfo(infoContext);
        handler.showParameterInfo(paramsOwner, infoContext);

        MockParameterInfoUIContext<PsiElement> context = new MockParameterInfoUIContext<>(paramsOwner);
        handler.updateUI((RmlParameterInfoHandler.ArgumentsDescription) infoContext.getItemsToShow()[0], context);

        MockUpdateParameterInfoContext updateContext = new MockUpdateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());
        RPsiParameters updateParamsOwner = handler.findElementForUpdatingParameterInfo(updateContext);
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
