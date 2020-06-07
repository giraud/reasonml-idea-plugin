package com.reason.lang.core;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightJavaCodeInsightTestCase;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;

public class ORSignatureTest extends LightJavaCodeInsightTestCase {

    private static final RmlLanguage RML = RmlLanguage.INSTANCE;
    private static final OclLanguage OCL = OclLanguage.INSTANCE;

    public void testReasonSingleFun() {
        ORSignature sig = makeSignature(RML, "unit=>unit", false);

        assertEquals("unit => unit", sig.asString(RML));
        assertEquals("unit -> unit", sig.asString(OCL));
    }

    public void testOCamlSingleFun() {
        ORSignature sig = makeSignature(OCL, "unit->unit", false);

        assertEquals("unit => unit", sig.asString(RML));
        assertEquals("unit -> unit", sig.asString(OCL));
    }

    public void testReasonSingle() {
        ORSignature sig = makeSignature(RML, "unit", false);

        assertEquals("unit", sig.asString(RML));
        assertEquals("unit", sig.asString(OCL));
    }

    public void testOCamlSingle() {
        ORSignature sig = makeSignature(OCL, "unit", false);

        assertEquals("unit", sig.asString(RML));
        assertEquals("unit", sig.asString(OCL));
    }

    public void testReasonMultiFun() {
        ORSignature sig = makeSignature(RML, "unit => string => float => unit", false);

        assertEquals("(unit, string, float) => unit", sig.asString(RML));
        assertEquals("unit -> string -> float -> unit", sig.asString(OCL));
    }

    public void testOcamlMultiFun() {
        ORSignature sig = makeSignature(OCL, "unit -> string -> float -> unit", false);

        assertEquals("(unit, string, float) => unit", sig.asString(RML));
        assertEquals("unit -> string -> float -> unit", sig.asString(OCL));
    }

    public void testOCamlObject() {
        ORSignature sig = makeSignature(OCL, "<a:string> -> string", false);

        assertEquals("<a:string> -> string", sig.asString(OCL));
        assertEquals("{. a:string } => string", sig.asString(RML));
    }

    public void testReasonJsObject() {
        ORSignature sig = makeSignature(RML, "{. a:string, b:int } => string", false);

        assertEquals("<a:string; b:int> Js.t -> string", sig.asString(OCL));
        assertEquals("{. a:string, b:int } => string", sig.asString(RML));
    }

    public void testOCamlJsObject() {
        ORSignature sig = makeSignature(OCL, "<a:string; b:int> Js.t -> string", false);

        assertEquals("<a:string; b:int> Js.t -> string", sig.asString(OCL));
        assertEquals("{. a:string, b:int } => string", sig.asString(RML));
    }

    public void testOcamJsJsObject() {
        ORSignature sig = makeSignature(OCL, "string -> < a : string; b : < b1 : string; b2 : string > Js.t > Js.t", false);

        assertEquals("string -> < a : string; b : < b1 : string; b2 : string > Js.t > Js.t", sig.asString(OCL));
        assertEquals("string => {. a:string, b:{. b1:string, b2:string } }", sig.asString(RML));
    }

    @SuppressWarnings({"SameParameterValue", "ConstantConditions"})
    @NotNull
    private ORSignature makeSignature(@NotNull Language lang, String sig, boolean debug) {
        PsiFileFactory instance = PsiFileFactory.getInstance(getProject());
        PsiFile psiFile = instance.createFileFromText("Dummy." + lang.getAssociatedFileType().getDefaultExtension(), lang, "let x:" + sig);
        if (debug) {
            System.out.println(DebugUtil.psiToString(psiFile, true, true));
        }
        Collection<PsiSignatureItem> items = PsiTreeUtil.findChildrenOfType(psiFile, PsiSignatureItem.class);
        return new ORSignature(RmlLanguage.INSTANCE, items);
    }
}
