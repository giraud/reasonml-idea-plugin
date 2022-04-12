package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

// Wrapper around the PsiBuilder.Marker to keep its status
public class MarkerScope {
    enum MarkerStatus {
        unset, done, dropped
    }

    private final PsiBuilder myBuilder;
    private final PsiBuilder.Marker myMark;
    private final int myOffset;

    private ORCompositeType myCompositeElementType;
    private ORTokenElementType myScopeElementType;
    private MarkerStatus myMarkerStatus = MarkerStatus.unset;
    private boolean myIsStart = false;

    private MarkerScope(@NotNull PsiBuilder builder, @NotNull PsiBuilder.Marker mark, @Nullable ORCompositeType compositeElementType, @Nullable ORTokenElementType scopeTokenElementType) {
        myBuilder = builder;
        myMark = mark;
        myOffset = builder.getCurrentOffset();
        myCompositeElementType = compositeElementType;
        myScopeElementType = scopeTokenElementType;
    }

    public static @NotNull MarkerScope mark(@NotNull PsiBuilder builder, @NotNull ORCompositeType compositeElementType) {
        return new MarkerScope(builder, builder.mark(), compositeElementType, null);
    }

    public static @NotNull MarkerScope precedeScope(@NotNull MarkerScope scope, @NotNull ORCompositeType compositeType) {
        PsiBuilder.Marker precede = scope.myMark.precede();
        return new MarkerScope(scope.myBuilder, precede, compositeType, null);
    }

    static @NotNull MarkerScope markRoot(@NotNull PsiBuilder builder) {
        return new MarkerScope(builder, builder.mark(), null, null);
    }

    public int getLength() {
        return myBuilder.getCurrentOffset() - myOffset;
    }

    public boolean isEmpty() {
        return getLength() == 0;
    }

    public void end() {
        done();
    }

    private void done() {
        if (myMarkerStatus == MarkerStatus.unset) {
            if (myCompositeElementType instanceof IElementType) {
                myMark.done((IElementType) myCompositeElementType);
                myMarkerStatus = MarkerStatus.done;
            } else {
                myMark.drop();
                myMarkerStatus = MarkerStatus.dropped;
            }
        }
    }

    public void drop() {
        if (myMarkerStatus == MarkerStatus.unset) {
            myMark.drop();
            myMarkerStatus = MarkerStatus.dropped;
        }
    }

    public void updateScope(@Nullable ORTokenElementType scopeToken) {
        myScopeElementType = scopeToken;
    }

    boolean isCompositeIn(ORCompositeType @NotNull ... compositeType) {
        for (ORCompositeType composite : compositeType) {
            if (myCompositeElementType == composite) {
                return true;
            }
        }
        return false;
    }

    public boolean isScopeToken(ORTokenElementType tokenElementType) {
        return myScopeElementType == tokenElementType;
    }

    void setScopeTokenType(@NotNull ORTokenElementType tokenElementType) {
        myScopeElementType = tokenElementType;
    }

    public void updateCompositeElementType(@NotNull ORCompositeType compositeType) {
        myCompositeElementType = compositeType;
    }

    public boolean isStart() {
        return myIsStart;
    }

    public void setIsStart(boolean isStart) {
        myIsStart = isStart;
    }

    public boolean hasScope() {
        return myScopeElementType != null;
    }

    @Nullable ORTokenElementType getScopeTokenElementType() {
        return myScopeElementType;
    }

    public void rollbackTo() {
        if (myMark != null) {
            myMark.rollbackTo();
        }
    }

    public boolean isCompositeType(@Nullable ORCompositeType elementType) {
        return myCompositeElementType == elementType;
    }

    public @Nullable ORCompositeType getCompositeType() {
        return myCompositeElementType;
    }

    @Override public String toString() {
        return myCompositeElementType + (myIsStart ? ".start" : "") + (myScopeElementType == null ? "" : " [" + myScopeElementType + "]") + " (" + myMarkerStatus + ")";
    }
}
