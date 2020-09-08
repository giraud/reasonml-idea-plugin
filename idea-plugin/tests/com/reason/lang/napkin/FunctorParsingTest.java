package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiConstraint;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiFunctorCall;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiParameter;

@SuppressWarnings("ConstantConditions")
public class FunctorParsingTest extends NsParsingTestCase {
    public void test_basic() {
        PsiNamedElement e = first(expressions(parseCode("module Make = (M: Def) : S => {}")));

        PsiFunctor f = (PsiFunctor) e;
        assertEquals("{}", f.getBinding().getText());
        assertEquals("S", f.getReturnType().getText());
    }

    public void test_withConstraints() {
        Collection<PsiNamedElement> expressions = expressions(parseCode("module Make = (M: Input) : (S with type t('a) = M.t('a) and type b = M.b) => {}"));

        assertEquals(1, expressions.size());
        PsiFunctor f = (PsiFunctor) first(expressions);

        assertEquals("M: Input", first(f.getParameters()).getText());
        assertEquals("S", f.getReturnType().getText());

        List<PsiConstraint> constraints = new ArrayList<>(f.getConstraints());
        assertEquals(2, constraints.size());
        assertEquals("type t('a) = M.t('a)", constraints.get(0).getText());
        assertEquals("type b = M.b", constraints.get(1).getText());
        assertEquals("{}", f.getBinding().getText());
    }

    public void test_signature() {
        Collection<PsiFunctor> functors = functorExpressions(parseCode(//
                                                                       "module GlobalBindings = (M: {\n" //
                                                                               + "           let relation_classes: list(string)\n"
                                                                               + "           let morphisms: list(string)\n"
                                                                               + "           let arrow: evars => evars\n"//
                                                                               + "         },\n"//
                                                                               + "       ) => {\n"//
                                                                               + "  open M\n"//
                                                                               + "}"));

        assertEquals(1, functors.size());
        PsiFunctor functor = first(functors);
        assertEquals("GlobalBindings", functor.getName());
        assertEquals("Dummy.GlobalBindings", functor.getQualifiedName());
        Collection<PsiParameter> parameters = functor.getParameters();
        assertSize(1, parameters);
        assertEquals("M", first(parameters).getName());
        assertNotNull(functor.getBinding());
    }

    public void test_functorInstanciation() {
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module Printing = Make({ let encode = encode_record })")));

        assertNull(module.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
        assertNotNull(call);
        assertEquals("Make({ let encode = encode_record })", call.getText());
    }

    public void test_functorInstantiationChaining() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash)\ntype infos");
        List<PsiNamedElement> expressions = new ArrayList<>(expressions(file));

        assertEquals(2, expressions.size());

        PsiInnerModule module = (PsiInnerModule) expressions.get(0);
        assertNull(module.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
        assertNotNull(call);
        assertEquals("Hashtbl.Make(KeyHash)", call.getText());
    }
}
