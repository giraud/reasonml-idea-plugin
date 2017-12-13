package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiFunBody;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiMacroName;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiToken;
import com.reason.lang.core.psi.impl.TypeImpl;
import com.reason.lang.core.psi.TagClose;
import com.reason.lang.core.psi.TagProperty;
import com.reason.lang.core.psi.TagStart;
import com.reason.lang.core.psi.impl.ModuleImpl;
import com.reason.lang.core.psi.impl.ModuleNameImpl;

class PsiElementFactory {
    static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();

        if (type == RmlTypes.EXTERNAL_EXPRESSION) {
            return new PsiExternal(node);
        } else if (type == RmlTypes.OPEN_EXPRESSION) {
            return new PsiOpen(node);
        } else if (type == RmlTypes.MODULE_EXPRESSION) {
            return new ModuleImpl(node);
        } else if (type == RmlTypes.MODULE_NAME) {
            return new ModuleNameImpl(node);
        } else if (type == RmlTypes.LET_EXPRESSION) {
            return new PsiLet(node);
        } else if (type == RmlTypes.ANNOTATION_EXPRESSION) {
            return new PsiAnnotation(node);
        } else if (type == RmlTypes.FUN_BODY) {
            return new PsiFunBody(node);
        } else if (type == RmlTypes.LET_BINDING) {
            return new PsiLetBinding(node);
        } else if (type == RmlTypes.MACRO_NAME) {
            return new PsiMacroName(node);
        } else if (type == RmlTypes.SCOPED_EXPR || type == RmlTypes.OBJECT_EXPR || type == RmlTypes.PATTERN_MATCH_EXPR) {
            return new PsiScopedExpr(node);
        } else if (type == RmlTypes.TYPE_EXPRESSION) {
            return new TypeImpl(node);
        } else if (type == RmlTypes.TAG_START) {
            return new TagStart(node);
        } else if (type == RmlTypes.TAG_PROPERTY) {
            return new TagProperty(node);
        } else if (type == RmlTypes.TAG_CLOSE) {
            return new TagClose(node);
        }

        return new PsiToken(node);
    }
}
