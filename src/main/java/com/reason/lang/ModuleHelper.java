package com.reason.lang;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.impl.RPsiAnnotation;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ModuleHelper {

    private ModuleHelper() {
    }

    public static boolean isComponent(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        PsiElement componentDef = null;
        RPsiLet makeDef = null;

        // JSX 3

        // Try to find a React.component attribute
        for (RPsiAnnotation annotation : PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiAnnotation.class)) {
            if ("@react.component".equals(annotation.getName())) {
                return true;
            }
        }

        // JSX 2

        // Try to find if it's a proxy to a React class
        List<RPsiExternal> externals = PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiExternal.class);
        for (RPsiExternal external : externals) {
            RPsiSignature signature = external.getSignature();
            String signatureText = signature == null ? null : signature.asText(ORLanguageProperties.cast(element.getLanguage()));
            if ("ReasonReact.reactClass".equals(signatureText)) {
                componentDef = external;
                break;
            }
        }

        // Try to find a make function and a component (if not a proxy) functions
        List<RPsiLet> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiLet.class);
        for (RPsiLet let : expressions) {
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
