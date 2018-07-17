package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.type.MlTokenElementType;

public class ParserScope {
    public ParserScopeEnum resolution;
    ParserScopeType scopeType = ParserScopeType.any;
    private IElementType compositeElementType;
    MlTokenElementType scopeTokenElementType;
    boolean complete = false;
    boolean start = false;

    private PsiBuilder.Marker mark;

    ParserScope(ParserScopeEnum resolution, IElementType compositeElementType, MlTokenElementType scopeTokenElementType, PsiBuilder.Marker mark) {
        this.resolution = resolution;
        this.compositeElementType = compositeElementType;
        this.scopeTokenElementType = scopeTokenElementType;
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
            if (compositeElementType != null) {
                mark.done(compositeElementType);
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

    boolean isCompositeEqualTo(IElementType compositeElementType) {
        return this.compositeElementType == compositeElementType;
    }

    boolean isScopeTokenEqualTo(MlTokenElementType tokenElementType) {
        return this.scopeTokenElementType == tokenElementType;
    }

    void setScopeTokenType(MlTokenElementType tokenElementType) {
        this.scopeTokenElementType = tokenElementType;
    }

    MlTokenElementType getScopeTokenType() {
        return this.scopeTokenElementType;
    }

    public void setCompositeElementType(IElementType compositeElementType) {
        this.compositeElementType = compositeElementType;
    }
}
