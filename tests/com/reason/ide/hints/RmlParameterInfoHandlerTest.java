package com.reason.ide.hints;

import com.intellij.psi.*;
import com.intellij.testFramework.utils.parameterInfo.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.impl.*;

public class RmlParameterInfoHandlerTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("A.rei", "let add : (int, int) => int;");
        configureCode("B.re", "A.add(<caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("(int, int) => int", context.text);
        assertEquals(0, context.currentParam);
    }

    public void test_intf_impl() {
        configureCode("A.rei", "let add : (int, int) => int;");
        configureCode("A.re", "let add = (x, y) => x + y;");
        configureCode("B.re", "A.add(<caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("(int, int) => int", context.text);
        assertEquals(0, context.currentParam);
    }

    public void test_empty() {
        configureCode("A.rei", "let fn : unit => string;");
        configureCode("B.re", "A.fn(<caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("unit => string", context.text);
        assertEquals(0, context.currentParam);
    }

    public void test_item() {
        configureCode("A.rei", "let add : (int, int) => int;");
        configureCode("B.re", "A.add(1, <caret>)");

        UIInfoContext context = getParameterInfoUI();
        assertEquals("(int, int) => int", context.text);
        assertEquals(1, context.currentParam);
    }

    @SuppressWarnings("ConstantConditions")
    private UIInfoContext getParameterInfoUI() {
        RmlParameterInfoHandler handler = new RmlParameterInfoHandler();
        MockCreateParameterInfoContext infoContext = new MockCreateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());

        PsiParameters paramsOwner = handler.findElementForParameterInfo(infoContext);
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
