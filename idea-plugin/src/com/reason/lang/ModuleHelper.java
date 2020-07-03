package com.reason.lang;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.napkin.NsLanguage;
import com.reason.lang.napkin.NsQNameFinder;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.ocaml.OclQNameFinder;
import com.reason.lang.reason.RmlQNameFinder;

public class ModuleHelper {

    private ModuleHelper() {
    }

    public static boolean isComponent(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        PsiElement componentDef = null;
        PsiLet makeDef = null;

        // JSX 3

        // Try to find a react.component attribute
        List<PsiAnnotation> annotations = PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiAnnotation.class);
        for (PsiAnnotation annotation : annotations) {
            if ("@react.component".equals(annotation.getName())) {
                return true;
            }
        }

        // JSX 2

        // Try to find if it's a proxy to a React class
        List<PsiExternal> externals = PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiExternal.class);
        for (PsiExternal external : externals) {
            if ("ReasonReact.reactClass".equals(external.getORSignature().asString(element.getLanguage()))) {
                componentDef = external;
                break;
            }
        }

        // Try to find a make function and a component (if not a proxy) functions
        List<PsiLet> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiLet.class);
        for (PsiLet let : expressions) {
            if (componentDef == null && "component".equals(let.getName())) {
                componentDef = let;
            } else if (makeDef == null && "make".equals(let.getName())) {
                makeDef = let;
            } else if (componentDef != null && makeDef != null) {
                break;
            }
        }

        return componentDef != null && makeDef != null;
    }
}
