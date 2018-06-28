package com.reason.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.ocaml.OclParserDefinition;

import java.util.Collection;
import java.util.Iterator;

public class LetParsingTest extends BaseParsingTestCase {
    public LetParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testConstant() {
        PsiLet let = first(parseCode("let x = 1").getLetExpressions());

        assertEquals("x", let.getName());
        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }


    public void testLetBinding() {
        PsiLet let = first(parseCode("let obj = [%bs.obj { a = \"b\" }];").getLetExpressions());

        assertFalse(let.isFunction());
        assertNotNull(first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class)));
    }

    public void testScopeWithSome() {
//  ?      PsiLet let = first(parseCode("let l = (p) => { switch (a) { | Some(a) => a; (); | None => () }; Some(z); };").getLetExpressions());
//
//        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
//        Assert.assertNotNull(binding);
    }

    public void testScopeWithLIdent() {
        PsiLet let = first(parseCode("let l p = Js.log p; returnObj").getLetExpressions());

        assertTrue(let.isFunction());
    }

    public void testRecord() {
        PsiLet let = first(parseCode("let r = { one = 1; two = 2 }").getLetExpressions());

        PsiLetBinding binding = first(PsiTreeUtil.findChildrenOfType(let, PsiLetBinding.class));
        assertNotNull(binding);
        PsiRecord record = PsiTreeUtil.findChildOfType(binding, PsiRecord.class);
        assertNotNull(record);
        Collection<PsiRecordField> fields = record.getFields();
        assertSize(2, fields);
        Iterator<PsiRecordField> itFields = fields.iterator();
        assertEquals("one = 1", itFields.next().getText());
        assertEquals("two = 2", itFields.next().getText());
    }

}
