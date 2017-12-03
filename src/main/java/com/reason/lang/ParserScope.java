package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

class ParserScope {
    ParserScopeEnum resolution;
    IElementType tokenType;
    ParserScopeType scopeType = ParserScopeType.any;
    boolean complete = true;

    private PsiBuilder.Marker mark;

    ParserScope(ParserScopeEnum resolution, IElementType tokenType, PsiBuilder.Marker mark) {
        this.resolution = resolution;
        this.tokenType = tokenType;
        this.mark = mark;
    }

    public void end() {
        if (complete) done();
        else drop();
    }

    private void done() {
        if (mark != null) {
            if (tokenType != null) {
                mark.done(tokenType);
            }
            else {
                mark.drop();
            }
            mark = null;
        }
    }

    private void drop() {
        if (mark != null) {
            mark.drop();
            mark = null;
        }
    }
}
