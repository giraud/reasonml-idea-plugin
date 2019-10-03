package com.reason.lang.core;

import com.intellij.lang.Language;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightCodeInsightTestCase;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ORSignatureTest extends LightCodeInsightTestCase {

    private static final RmlLanguage RML = RmlLanguage.INSTANCE;
    private static final OclLanguage OCL = OclLanguage.INSTANCE;

    public void testReasonSingleFun() {
        ORSignature sig = makeSignature(RML, "unit=>unit");

        assertEquals("unit => unit", sig.asString(RML));
        assertEquals("unit -> unit", sig.asString(OCL));
    }

    public void testOCamlSingleFun() {
        ORSignature sig = makeSignature(OCL, "unit->unit");

        assertEquals("unit => unit", sig.asString(RML));
        assertEquals("unit -> unit", sig.asString(OCL));
    }

    public void testReasonSingle() {
        ORSignature sig = makeSignature(RML, "unit");

        assertEquals("unit", sig.asString(RML));
        assertEquals("unit", sig.asString(OCL));
    }

    public void testOCamlSingle() {
        ORSignature sig = makeSignature(OCL, "unit");

        assertEquals("unit", sig.asString(RML));
        assertEquals("unit", sig.asString(OCL));
    }

    public void testReasonMultiFun() {
        ORSignature sig = makeSignature(RML, "unit => string => float => unit");

        assertEquals("(unit, string, float) => unit", sig.asString(RML));
        assertEquals("unit -> string -> float -> unit", sig.asString(OCL));
    }

    public void testOcamlMultiFun() {
        ORSignature sig = makeSignature(OCL, "unit -> string -> float -> unit");

        assertEquals("(unit, string, float) => unit", sig.asString(RML));
        assertEquals("unit -> string -> float -> unit", sig.asString(OCL));
    }

    public void testOCamlObject() {
        ORSignature sig = makeSignature(OCL, "<a:string> -> string");

        assertEquals("<a:string> -> string", sig.asString(OCL));
        assertEquals("{. a:string } => string", sig.asString(RML));
    }

    public void testOCamlJsObject() {
        ORSignature sig = makeSignature(OCL, "<a:string; b:int> Js.t -> string");

        assertEquals("<a:string; b:int> Js.t -> string", sig.asString(OCL));
        assertEquals("{. \"a\":string, \"b\":int } => string", sig.asString(RML));
    }

    /*
    public void ReasonParam() {
//TODO        assertEquals("show(bool)", (new ORSignature("bool show")).asString(RmlLanguage.INSTANCE));
    }
*/

    @NotNull
    private ORSignature makeSignature(Language lang, String sig) {
        PsiFileFactory instance = PsiFileFactory.getInstance(getProject());
        PsiFile psiFile = instance.createFileFromText("Dummy." + lang.getAssociatedFileType().getDefaultExtension(), lang, "let x:" + sig);
        Collection<PsiSignatureItem> items = PsiTreeUtil.findChildrenOfType(psiFile, PsiSignatureItem.class);
        return new ORSignature(RmlLanguage.INSTANCE, items);
    }
}
