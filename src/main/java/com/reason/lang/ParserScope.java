package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

public class ParserScope {
    public ParserScopeEnum resolution;
    public IElementType tokenType;
    public ParserScopeType scopeType = ParserScopeType.any;
    IElementType scopeElementType;
    public boolean complete = false;

    private PsiBuilder.Marker mark;

    ParserScope(ParserScopeEnum resolution, IElementType tokenType, PsiBuilder.Marker mark) {
        this.resolution = resolution;
        this.tokenType = tokenType;
        this.mark = mark;
    }

    public void end() {
        if (complete) {
            done();
        } else {
            drop();
        }
    }

    private void done() {
        if (mark != null) {
            if (tokenType != null) {
                mark.done(tokenType);
            } else {
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
