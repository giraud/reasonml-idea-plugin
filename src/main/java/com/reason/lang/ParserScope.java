package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

class ParserScope {
    ParserScopeEnum resolution;
    IElementType tokenType;
    boolean includeSemi = true;
    boolean complete = false;
    private PsiBuilder.Marker mark;
    public boolean isExpression = false;

    ParserScope(ParserScopeEnum resolution, IElementType tokenType, PsiBuilder.Marker mark) {
        this.resolution = resolution;
        this.tokenType = tokenType;
        this.mark = mark;
    }

    public void drop() {
        if (mark != null) {
            mark.drop();
            mark = null;
        }
    }

    public void done() {
        if (mark != null && tokenType != null) {
            mark.done(tokenType);
            mark = null;
        }
    }

    public void end() {
        if (complete) done();
        else drop();
    }
}
