package com.reason.lang;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModuleHelper {

    private ModuleHelper() {
    }

    public static boolean isComponent(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        PsiElement componentDef = null;
        PsiLet makeDef = null;

        // Try to find if it's a proxy to a react class
        List<PsiExternal> externals = PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiExternal.class);
        for (PsiExternal external : externals) {
            if ("ReasonReact.reactClass".equals(external.getHMSignature().asString(element.getLanguage()))) {
                componentDef = external;
                break;
            }
        }

        // Try to find a make and a component (if not a proxy) functions
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
