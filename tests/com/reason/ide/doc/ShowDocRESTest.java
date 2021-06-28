package com.reason.ide.doc;

import com.intellij.lang.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.rescript.*;

public class ShowDocRESTest extends ORBasePlatformTestCase {
    public static final Language LANG = ResLanguage.INSTANCE;

    public void test_GH_155() {
        FileBase doc = configureCode("Doc.res", "/** add 1 */\nlet fn = x => x + 1");
        FileBase a = configureCode("A.res", "Mod.fn(<caret>)");

        PsiLowerIdentifier let = ORUtil.findImmediateFirstChildOfClass(doc.getQualifiedExpressions("Doc.fn", PsiLet.class).get(0), PsiLowerIdentifier.class);
        // zzz trim
        assertEquals("<div style='padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE'>Doc</div><div><p> add 1 </p></div>", getDocForElement(a, LANG, let));
    }

    public void test_GH_156() {
        configureCode("Doc.res", "/** Doc for y */\nlet y = 1");
        FileBase a = configureCode("A.res", "let x = Doc.y\nx<caret>");

        // zzz trim
        assertEquals("<div style='padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE'>Doc</div><div><p> Doc for y </p></div>", getDoc(a, LANG));
    }
}
