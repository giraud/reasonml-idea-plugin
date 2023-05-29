package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

// Wrapper around the PsiBuilder.Marker to keep its status
public class Marker {
    enum Status {
        unset, atom, hold, done, dropped
    }

    private final PsiBuilder myBuilder;
    private final PsiBuilder.Marker myMark;
    private final int myOffset;

    private ORCompositeType myCompositeElementType;
    private IElementType myScopeElementType;
    private IElementType myAtomType;
    private Status myStatus = Status.unset;

    private Marker(@NotNull PsiBuilder builder, @NotNull PsiBuilder.Marker mark, @NotNull ORCompositeType compositeElementType, @Nullable IElementType scopeTokenElementType) {
        myBuilder = builder;
        myMark = mark;
        myOffset = builder.getCurrentOffset();
        myCompositeElementType = compositeElementType;
        myScopeElementType = scopeTokenElementType;
    }

    public static @NotNull Marker mark(@NotNull PsiBuilder builder, @NotNull ORCompositeType compositeElementType) {
        return new Marker(builder, builder.mark(), compositeElementType, null);
    }

    public static @NotNull Marker atom(@NotNull PsiBuilder builder, @NotNull ORCompositeType atomElementType) {
        Marker marker = new Marker(builder, builder.mark(), atomElementType, null);
        marker.myAtomType = builder.getTokenType();
        marker.myStatus = Status.atom;
        return marker;
    }

    public static @NotNull Marker precede(@NotNull Marker mark, @NotNull ORCompositeType compositeType) {
        PsiBuilder.Marker precede = mark.myMark.precede();
        return new Marker(mark.myBuilder, precede, compositeType, null);
    }

    public static Marker duplicate(Marker marker) {
        Marker newMarker = new Marker(marker.myBuilder, marker.myBuilder.mark(), marker.myCompositeElementType, marker.myScopeElementType);
        newMarker.myStatus = marker.myStatus;

        if (marker.isDone()) {
            newMarker.done();
        } else if (marker.isDropped()) {
            newMarker.drop();
        }

        return newMarker;
    }

    public int getLength() {
        return myBuilder.getCurrentOffset() - myOffset;
    }

    public boolean isEmpty() {
        return getLength() == 0;
    }

    public void end() {
        done();
        myScopeElementType = null;
    }

    private void done() {
        if (myCompositeElementType instanceof IElementType) {
            if (myStatus == Status.unset) {
                if (myCompositeElementType.toString().startsWith("D_")) {
                    myMark.drop();
                    myStatus = Status.dropped;
                } else {
                    myMark.done((IElementType) myCompositeElementType);
                    myStatus = Status.done;
                }
            } else if (myStatus == Status.atom && myAtomType != null) {
                myMark.collapse(myAtomType);
                myStatus = Status.done;
            }
        }

        if (myStatus != Status.done && myStatus != Status.dropped) {
            myMark.drop();
            myStatus = Status.dropped;
        }
    }

    public void drop() {
        if (myStatus == Status.unset) {
            myMark.drop();
            myStatus = Status.dropped;
        }
    }

    public void hold() {
        if (myStatus == Status.unset) {
            myStatus = Status.hold;
        }
    }

    public void updateCompositeType(@NotNull ORCompositeType compositeType) {
        myCompositeElementType = compositeType;
    }

    boolean isCompositeIn(ORCompositeType @NotNull ... compositeType) {
        for (ORCompositeType composite : compositeType) {
            if (myCompositeElementType == composite) {
                return true;
            }
        }
        return false;
    }

    void setScopeType(@NotNull IElementType scopeType) {
        myScopeElementType = scopeType;
    }

    public void updateScope(@Nullable IElementType scopeToken) {
        myScopeElementType = scopeToken;
    }

    public boolean isScopeToken(ORTokenElementType scopeType) {
        return myScopeElementType == scopeType;
    }

    public @Nullable IElementType getScopeType() {
        return myScopeElementType;
    }

    public boolean hasScope() {
        return myScopeElementType != null;
    }

    public void rollbackTo() {
        if (myMark != null) {
            myMark.rollbackTo();
        }
    }

    public boolean isCompositeType(@Nullable ORCompositeType elementType) {
        return myCompositeElementType == elementType;
    }

    public @NotNull ORCompositeType getCompositeType() {
        return myCompositeElementType;
    }

    public boolean isUnset() {
        return myStatus == Status.unset;
    }

    public boolean isDone() {
        return myStatus == Status.done;
    }

    public boolean isDropped() {
        return myStatus == Status.dropped;
    }

    public boolean isHold() {
        return myStatus == Status.hold;
    }

    public void resetStatus() {
        if (myStatus != Status.dropped && myStatus != Status.done) {
            myStatus = Status.unset;
        }
    }
}
