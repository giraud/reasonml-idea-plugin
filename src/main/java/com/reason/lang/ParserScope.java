package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

public class ParserScope {
    public ParserScopeEnum resolution;
    ParserScopeType scopeType = ParserScopeType.any;
    public IElementType tokenType;
    IElementType scopeElementType;
    boolean complete = false;
    boolean start = false;

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

    public void complete() {
        complete = true;
    }
}
