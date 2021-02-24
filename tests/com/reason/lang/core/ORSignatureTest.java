package com.reason.lang.core;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.util.*;
import com.intellij.testFramework.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.napkin.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class ORSignatureTest extends LightJavaCodeInsightTestCase {

    private static final ResLanguage NS = ResLanguage.INSTANCE;
    private static final RmlLanguage RML = RmlLanguage.INSTANCE;
    private static final OclLanguage OCL = OclLanguage.INSTANCE;

    public void testReasonSingleFun() {
        PsiSignature sig = makeSignature(RML, "unit=>unit");

        assertEquals("unit => unit", sig.asText(RML));
        assertEquals("unit -> unit", sig.asText(OCL));
    }

    public void testOCamlSingleFun() {
        PsiSignature sig = makeSignature(OCL, "unit->unit");

        assertEquals("unit => unit", sig.asText(RML));
        assertEquals("unit -> unit", sig.asText(OCL));
    }

    public void testReasonSingle() {
        PsiSignature sig = makeSignature(RML, "unit");

        assertEquals("unit", sig.asText(RML));
        assertEquals("unit", sig.asText(OCL));
    }

    public void testOCamlSingle() {
        PsiSignature sig = makeSignature(OCL, "unit");

        assertEquals("unit", sig.asText(RML));
        assertEquals("unit", sig.asText(OCL));
    }

    public void testReasonMultiFun() {
        PsiSignature sig = makeSignature(RML, "unit => string => float => unit");

        assertEquals("(unit, string, float) => unit", sig.asText(RML));
        assertEquals("unit -> string -> float -> unit", sig.asText(OCL));
    }

    public void testOcamlMultiFun() {
        PsiSignature sig = makeSignature(OCL, "unit -> string -> float -> unit");

        assertEquals("(unit, string, float) => unit", sig.asText(RML));
        assertEquals("unit -> string -> float -> unit", sig.asText(OCL));
    }

    public void testOCamlObject() {
        PsiSignature sig = makeSignature(OCL, "<a:string> -> string");

        assertEquals("<a:string> -> string", sig.asText(OCL));
        assertEquals("{. a:string } => string", sig.asText(RML));
    }

    public void testReasonJsObject() {
        PsiSignature sig = makeSignature(RML, "{. a:string, b:int } => string");

        assertEquals("<a:string; b:int> Js.t -> string", sig.asText(OCL));
        assertEquals("{. a:string, b:int } => string", sig.asText(RML));
        //assertEquals("{. a:string, b:int } => string", sig.asText(NS));
    }

    public void testOCamlJsObject() {
        PsiSignature sig = makeSignature(OCL, "<a:string; b:int> Js.t -> string");

        assertEquals("<a:string; b:int> Js.t -> string", sig.asText(OCL));
        assertEquals("{. a:string, b:int } => string", sig.asText(RML));
    }

    public void testOcamJsJsObject() {
        PsiSignature sig =
                makeSignature(OCL, "string -> < a : string; b : < b1 : string; b2 : string > Js.t > Js.t");

        assertEquals(
                "string -> < a : string; b : < b1 : string; b2 : string > Js.t > Js.t", sig.asText(OCL));
        assertEquals("string => {. a:string, b:{. b1:string, b2:string } }", sig.asText(RML));
    }

    @SuppressWarnings({"SameParameterValue", "ConstantConditions"})
    private @NotNull PsiSignature makeSignature(@NotNull Language lang, String sig) {
        PsiFileFactory instance = PsiFileFactory.getInstance(getProject());
        PsiFile psiFile = instance.createFileFromText("Dummy." + lang.getAssociatedFileType().getDefaultExtension(), lang, "let x:" + sig);
        System.out.println(DebugUtil.psiToString(psiFile, true, true));

        return PsiTreeUtil.findChildOfType(psiFile, PsiSignature.class);
    }
}
