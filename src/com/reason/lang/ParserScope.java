package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

// Wrapper around the PsiBuilder.Marker to keep its status
public class ParserScope {
    // zzz MarkerScope
    enum MarkerStatus {
        unset, done, dropped
    }

    private final PsiBuilder myBuilder;
    private ORCompositeType myCompositeElementType;
    private ORTokenElementType myScopeElementType;
    private MarkerStatus myMarkerStatus = MarkerStatus.unset;
    private PsiBuilder.Marker myMark;

    // zzz
    private final int m_offset;
    private ParserScopeEnum m_resolution;
    private boolean m_isComplete = true;
    private boolean m_isDummy = false; // Always drop
    private boolean m_isStart = false;

    private ParserScope(@NotNull PsiBuilder builder, @NotNull PsiBuilder.Marker mark, @Nullable ORCompositeType compositeElementType, @Nullable ORTokenElementType scopeTokenElementType) {
        myBuilder = builder;
        myMark = mark;
        m_offset = builder.getCurrentOffset();
        myCompositeElementType = compositeElementType;
        myScopeElementType = scopeTokenElementType;
    }

    //public static @NotNull ParserScope copy(@NotNull ParserScope scope) {
    //    return new ParserScope(scope.m_builder, scope.m_builder.mark(), scope.myCompositeElementType, scope.myScopeElementType);
    //}

    public static @NotNull ParserScope mark(@NotNull PsiBuilder builder, @NotNull ORCompositeType compositeElementType) {
        return new ParserScope(builder, builder.mark(), compositeElementType, null);
    }

    public static @NotNull ParserScope precedeScope(@NotNull ParserScope scope, @NotNull ORCompositeType compositeType) {
        PsiBuilder.Marker precede = scope.myMark.precede();
        return new ParserScope(scope.myBuilder, precede, compositeType, null);
    }

    //public static @NotNull ParserScope precedeMark(@NotNull PsiBuilder builder, @NotNull PsiBuilder.Marker mark, @NotNull ORCompositeType compositeType) {
//        PsiBuilder.Marker precede = mark.precede();
    //      return new ParserScope(builder, precede, compositeType, null);
    // }

    static @NotNull ParserScope markRoot(@NotNull PsiBuilder builder) {
        return new ParserScope(builder, builder.mark(), null, null);
    }

    public int getLength() {
        return myBuilder.getCurrentOffset() - m_offset;
    }

    public boolean isEmpty() {
        return getLength() == 0;
    }

    public void end() {
        if (m_isDummy) {
            drop();
        } else if (m_isComplete) {
            done();
        } else {
            drop();
        }
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

    void drop() {
        if (myMarkerStatus != MarkerStatus.unset) {
            myMark.drop();
            myMarkerStatus = MarkerStatus.dropped;
        }
    }

    public @NotNull ParserScope complete() {
        m_isComplete = true;
        return this;
    }

    public @NotNull ParserScope optional() {
        m_isComplete = false;
        return this;
    }

    public boolean isOptional() {
        return !m_isComplete;
    }

    public void updateScope(ORTokenElementType scopeToken) {
        myScopeElementType = scopeToken;
    }

    public void dummy() {
        m_isComplete = false;
        m_isDummy = true;
    }

    public boolean isResolution(ParserScopeEnum resolution) {
        return m_resolution == resolution;
    }

    @NotNull
    public ParserScope resolution(@Nullable ParserScopeEnum resolution) {
        m_resolution = resolution;
        return this;
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
        m_isDummy = false;
    }

    public boolean isStart() {
        return m_isStart;
    }

    public void setIsStart(boolean isStart) {
        m_isStart = isStart;
    }

    public @Nullable ParserScopeEnum getResolution() {
        return m_resolution;
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

    public @Nullable PsiBuilder.Marker precede() {
        if (myMark != null) {
            return myMark.precede();
        }
        return null;
    }

    public boolean isCompositeType(ORCompositeType elementType) {
        return myCompositeElementType == elementType;
    }

    public @Nullable ORCompositeType getCompositeType() {
        return myCompositeElementType;
    }

    public @Nullable ORTokenElementType getScopeType() {
        return myScopeElementType;
    }

    public boolean isDummy() {
        return m_isDummy;
    }

    public void remapComposite(@NotNull ORCompositeType composite) {
        if (myMarkerStatus == MarkerStatus.done) {
            myMark.drop();
        } else {
            myCompositeElementType = composite;
        }
    }

    @Override public String toString() {
        return myCompositeElementType + (m_isStart ? ".start" : "") + (myScopeElementType == null ? "" : " [" + myScopeElementType + "]") + " (" + myMarkerStatus + ")"
                //"m_builder=" + m_builder +
                //", m_offset=" + m_offset +
                //", m_resolution=" + m_resolution +
                //", m_compositeElementType=" +
                //", m_scopeTokenElementType=" + m_scopeTokenElementType +
                //", m_isComplete=" +
                //", m_isDummy=" + m_isDummy +
                //", m_isStart=" + m_isStart +
                //", m_mark=" + m_mark +
                //'}';
                ;
    }
}
