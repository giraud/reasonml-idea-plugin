package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class ORParser<T extends ORTypes> {
    protected static final Log LOG = Log.create("parser");

    protected final T myTypes;
    protected final boolean myVerbose;
    protected final boolean myIsSafe;
    protected final PsiBuilder myBuilder;
    protected final LinkedList<Marker> myMarkers = new LinkedList<>();
    public boolean dontMove = false;
    private int myIndex; // found index when using in(..)/inAny(..) functions

    protected ORParser(@NotNull T types, @NotNull PsiBuilder builder, boolean isSafe) {
        myTypes = types;
        myBuilder = builder;
        myIsSafe = isSafe;
        myVerbose = !isSafe;
    }

    public abstract void parse();

    public abstract void eof();

    public @Nullable IElementType previousElementType(int step) {
        int pos = -1;
        int found = 0;
        int total = Math.abs(step);

        IElementType elementType = myBuilder.rawLookup(pos);
        if (elementType != null && elementType != myTypes.WHITE_SPACE && elementType != myTypes.SINGLE_COMMENT && elementType != myTypes.MULTI_COMMENT) {
            found++;
        }

        while (elementType != null && found != total) {
            pos--;
            elementType = myBuilder.rawLookup(pos);
            if (elementType != null && elementType != myTypes.WHITE_SPACE && elementType != myTypes.SINGLE_COMMENT && elementType != myTypes.MULTI_COMMENT) {
                found++;
            }
        }

        return elementType;
    }

    public @NotNull ORParser<T> popEndUntilStart() {
        Marker latestKnownScope;

        if (!myMarkers.isEmpty()) {
            latestKnownScope = myMarkers.peek();
            Marker scope = latestKnownScope;
            while (scope != null && !scope.isStart()) {
                scope = pop();
                if (scope != null) {
                    if (scope.isEmpty()) {
                        scope.drop();
                    } else {
                        scope.end();
                    }
                }
                scope = getLatestMarker();
            }
        }

        return this;
    }

    public @Nullable Marker popEndUntilScope() {
        Marker latestKnownScope = null;

        if (!myMarkers.isEmpty()) {
            latestKnownScope = myMarkers.peek();
            Marker marker = latestKnownScope;
            while (marker != null && !marker.hasScope()) {
                marker = pop();
                if (marker != null) {
                    marker.end();
                    latestKnownScope = marker;
                }
                marker = getLatestMarker();
            }
        }

        return latestKnownScope;
    }

    public @Nullable Marker popEndUntilScopeToken(@NotNull ORTokenElementType scopeElementType) {
        Marker scope = null;

        if (!myMarkers.isEmpty()) {
            scope = myMarkers.peek();
            while (scope != null && scope.getScopeType() != scopeElementType) {
                popEnd();
                scope = getLatestMarker();
            }
        }

        return scope;
    }

    public @Nullable Marker getLatestMarker() {
        return myMarkers.isEmpty() ? null : myMarkers.peek();
    }

    public @Nullable Marker getCurrentMarker() {
        for (Marker marker : myMarkers) {
            if (!marker.isHold()) {
                return marker;
            }
        }
        return null;
    }

    public @Nullable Marker getActiveMarker() { // latest unset, not hold marker
        for (Marker marker : myMarkers) {
            if (marker.isUnset()) {
                return marker;
            }
        }
        return null;
    }

    public boolean is(@Nullable ORCompositeType composite) {
        return !myMarkers.isEmpty() && myMarkers.peek().isCompositeType(composite);
    }

    public boolean isCurrent(@Nullable ORCompositeType composite) {
        // skip done/drop/hold elements
        for (Marker marker : myMarkers) {
            if (marker.isUnset() && !marker.isHold()) {
                return marker.isCompositeType(composite);
            }
        }
        return false;
    }

    public boolean isDone(@Nullable ORCompositeType composite) {
        Marker marker = myMarkers.isEmpty() ? null : myMarkers.peek();
        return marker != null && marker.isDone() && marker.isCompositeType(composite);
    }

    public boolean isRawParent(ORCompositeType composite) {
        boolean found = false;
        if (myMarkers.size() >= 2) {
            // current index is 0
            found = myMarkers.get(1).isCompositeType(composite);
        }
        myIndex = found ? 1 : -1;
        return found;
    }

    public boolean isParent(ORCompositeType expectedType) {
        boolean found = false;
        int markersCount = myMarkers.size();

        // find start
        int startIndex = 0;
        while (startIndex < markersCount && myMarkers.get(startIndex).isHold()) {
            startIndex++;
        }

        // find parent
        int parentIndex = startIndex + 1;
        while (parentIndex < markersCount && !found) {
            if (myMarkers.get(parentIndex).isHold()) {
                parentIndex++;
            } else {
                found = true;
            }
        }

        // parent found, try type
        if (found && myMarkers.get(parentIndex).isCompositeType(expectedType)) {
            myIndex = parentIndex;
            return true;
        }

        myIndex = -1;
        return false;
    }

    public boolean isPrevious(ORCompositeType expectedComposite, int index) {
        int size = myMarkers.size() - 1;
        if (index >= 0 && index < size) {
            return myMarkers.get(index + 1).isCompositeType(expectedComposite);
        }
        return false;
    }

    public boolean isRawGrandParent(@Nullable ORCompositeType composite) {
        if (myMarkers.size() >= 3) {
            return myMarkers.get(2).isCompositeType(composite);
        }
        return false;
    }

    public boolean in(ORCompositeType composite) {
        return in(composite, null, myMarkers.size(), false);
    }

    public boolean in(ORCompositeType composite, @Nullable ORCompositeType excluded) {
        return in(composite, excluded, myMarkers.size(), false);
    }

    public boolean in(ORCompositeType composite, @Nullable ORCompositeType excluded, int maxDepth, boolean useScope) {
        int size = myMarkers.size();
        int stop = Math.min(size, maxDepth);
        for (int i = 0; i < stop; i++) {
            Marker marker = myMarkers.get(i);
            if (marker.isUnset() || marker.isHold()) { // not dropped or done
                if (marker.isCompositeType(excluded)) {
                    myIndex = -1;
                    return false;
                }
                if ((useScope && marker.hasScope()) || marker.isCompositeType(composite)) {
                    myIndex = i;
                    return true;
                }
            }
        }
        myIndex = -1;
        return false;
    }

    public boolean strictlyIn(ORCompositeType composite) {
        for (int i = 0; i < myMarkers.size(); i++) {
            Marker marker = myMarkers.get(i);
            if (marker.isUnset()) { // not dropped or done
                if (marker.isCompositeType(composite)) {
                    myIndex = i;
                    return true;
                }
                if (marker.hasScope()) {
                    myIndex = -1;
                    return false;
                }
            }
        }
        myIndex = -1;
        return false;
    }

    public boolean strictlyInAny(@NotNull ORCompositeType... composite) {
        for (int i = 0; i < myMarkers.size(); i++) {
            Marker marker = myMarkers.get(i);
            if (marker.isUnset() || marker.isHold()) { // not dropped or done
                if (marker.isCompositeIn(composite)) {
                    myIndex = i;
                    return true;
                }
                if (marker.hasScope()) {
                    myIndex = -1;
                    return false;
                }
            }
        }
        myIndex = -1;
        return false;
    }

    public @Nullable Marker find(int index) {
        return (0 <= index && index < myMarkers.size()) ? myMarkers.get(index) : null;
    }

    public int indexOfComposite(@NotNull ORCompositeType composite) {
        for (int i = myMarkers.size() - 1; i >= 0; i--) {
            Marker markerScope = myMarkers.get(i);
            if (markerScope.isCompositeType(composite)) {
                return i;
            }
        }
        return -1;
    }

    public int latestIndexOfCompositeAtMost(@NotNull ORCompositeType composite, int maxIndex) {
        int max = Math.min(maxIndex, myMarkers.size());
        for (int i = 0; i < max; i++) {
            Marker markerScope = myMarkers.get(i);
            if (markerScope.isCompositeType(composite)) {
                return i;
            }
        }
        return -1;
    }

    public boolean inAny(ORCompositeType... composite) {
        int stop = myMarkers.size();
        for (int i = 0; i < stop; i++) {
            Marker markerScope = myMarkers.get(i);
            if (markerScope.isCompositeIn(composite)) {
                myIndex = i;
                return true;
            }
        }
        myIndex = -1;
        return false;
    }

    public boolean inScopeOrAny(ORCompositeType... composite) {
        int stop = myMarkers.size();
        for (int i = 0; i < stop; i++) {
            Marker markerScope = myMarkers.get(i);
            if (markerScope.hasScope() || markerScope.isCompositeIn(composite)) {
                myIndex = i;
                return true;
            }
        }
        myIndex = -1;
        return false;
    }

    public boolean isScope(@Nullable ORTokenElementType scopeType) {
        Marker latest = getLatestMarker();
        return latest != null && latest.isScopeToken(scopeType);
    }

    public boolean isScopeAtIndex(int pos, @Nullable ORTokenElementType scopeType) {
        Marker marker = find(pos);
        return marker != null && marker.isScopeToken(scopeType);
    }

    public boolean isCurrentScope(@Nullable ORTokenElementType scopeType) {
        Marker latest = getCurrentMarker();
        return latest != null && latest.isScopeToken(scopeType);
    }

    protected boolean isFoundScope(@Nullable ORTokenElementType expectedScope) {
        Marker found = find(myIndex);
        return found != null && found.isScopeToken(expectedScope);
    }

    public @NotNull ORParser<T> mark(@NotNull ORCompositeType composite) {
        Marker mark = Marker.mark(myBuilder, composite);
        myMarkers.push(mark);
        return this;
    }

    public @NotNull ORParser<T> markScope(@NotNull ORCompositeType composite, @Nullable ORTokenElementType scope) {
        mark(composite).updateScopeToken(scope);
        return this;
    }

    public @NotNull ORParser<T> markDummyScope(@NotNull ORCompositeType composite, @NotNull ORTokenElementType scope) {
        markScope(composite, scope);
        Marker latest = myMarkers.peek();
        if (latest != null) {
            latest.hold();
        }
        return this;
    }

    public ORParser<T> markHolder(@NotNull ORCompositeType compositeType) {
        Marker mark = Marker.mark(myBuilder, compositeType);
        mark.hold();
        myMarkers.push(mark);
        return this;
    }

    protected ORParser<T> markHolderBefore(int pos, @NotNull ORCompositeType compositeType) {
        markBefore(pos, compositeType);
        Marker mark = myMarkers.get(pos + 1);
        assert mark != null;
        mark.hold();
        return this;
    }

    boolean empty() {
        return myMarkers.isEmpty();
    }

    public @Nullable Marker pop() {
        return myMarkers.isEmpty() ? null : myMarkers.pop();
    }

    void clear() {
        Marker scope = pop();
        while (scope != null) {
            scope.end();
            scope = pop();
        }
    }

    public ORParser<T> popIfHold() {
        if (isHold()) {
            popEnd();
        }
        return this;
    }

    public @NotNull ORParser<T> popEnd() {
        Marker scope = pop();
        if (scope != null) {
            scope.end();
        }
        return this;
    }


    public void popEndUntilOneOf(@NotNull ORCompositeType... composites) {
        if (!myMarkers.isEmpty()) {
            Marker marker = myMarkers.peek();
            while (marker != null && !ArrayUtil.contains(marker.getCompositeType(), composites)) {
                popEnd();
                marker = getLatestMarker();
            }
        }
    }

    @Nullable
    public Marker popEndUntilOneOfElementType(@NotNull ORTokenElementType... scopeElementTypes) {
        Marker marker = null;

        if (!myMarkers.isEmpty()) {
            marker = myMarkers.peek();
            while (marker != null && !ArrayUtil.contains(marker.getScopeType(), scopeElementTypes)) {
                popEnd();
                marker = getLatestMarker();
            }
        }

        return marker;
    }

    @NotNull
    public ORParser<T> popEndUntil(@NotNull ORCompositeType composite) {
        Marker marker = getLatestMarker();
        while (marker != null && !marker.isCompositeType(composite)) {
            popEnd();
            marker = getLatestMarker();
        }

        return this;
    }

    public ORParser<T> popEndUntilIndex(int index) {
        if (0 <= index && index <= myMarkers.size()) {
            for (int i = 0; i < index; i++) {
                popEnd();
            }
        }
        return this;
    }

    public ORParser<T> popEndUntilFoundIndex() {
        int index = myIndex;
        myIndex = -1;
        return popEndUntilIndex(index);
    }

    public boolean rawHasScope() {
        Marker marker = getLatestMarker();
        return marker != null && marker.hasScope();
    }

    protected boolean currentHasScope() {
        Marker current = getCurrentMarker();
        return current != null && current.hasScope();
    }

    public @NotNull ORParser<T> updateLatestComposite(@NotNull ORCompositeType compositeElementType) {
        Marker marker = getLatestMarker();
        if (marker != null && !marker.isDropped() && !marker.isDone()) {
            marker.updateCompositeType(compositeElementType);
            marker.resetStatus();
        }
        return this;
    }

    public @NotNull ORParser<T> updateComposite(@NotNull ORCompositeType compositeElementType) {
        Marker marker = getCurrentMarker();
        if (marker != null && !marker.isDropped() && !marker.isDone()) {
            marker.updateCompositeType(compositeElementType);
            marker.resetStatus();
        }
        return this;
    }

    public @NotNull ORParser<T> updateToHolder() {
        Marker marker = getCurrentMarker();
        if (marker != null) {
            marker.hold();
        }
        return this;
    }

    public @NotNull ORParser<T> advance() {
        myBuilder.advanceLexer();
        dontMove = true;
        return this;
    }

    public @NotNull ORParser<T> updateScopeToken(@Nullable ORTokenElementType token) {
        if (token != null) {
            Marker marker = getLatestMarker();
            if (marker != null) {
                marker.setScopeType(token);
            }
        }
        return this;
    }

    public @NotNull ORParser<T> wrapWith(@NotNull ORCompositeType compositeType) {
        mark(compositeType).advance();
        myMarkers.getFirst().end();
        return this;
    }

    public @NotNull ORParser<T> wrapAtom(@NotNull ORCompositeType atomCompositeType) {
        Marker mark = Marker.atom(myBuilder, atomCompositeType);
        myMarkers.push(mark);
        advance();
        mark.end();
        return this;
    }

    public void setStart() {
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.setIsStart(true);
        }
    }

    public @NotNull ORParser<T> setStart(boolean isStart) {
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.setIsStart(isStart);
        }
        return this;
    }

    public void error(@NotNull String message) {
        myBuilder.error("Plugin error! " + message);
    }

    public @NotNull ORParser<T> remapCurrentToken(ORTokenElementType elementType) {
        myBuilder.remapCurrentToken(elementType);
        return this;
    }

    public @NotNull ORParser<T> setWhitespaceSkippedCallback(@Nullable WhitespaceSkippedCallback callback) {
        myBuilder.setWhitespaceSkippedCallback(callback);
        return this;
    }

    public @Nullable String getTokenText() {
        return myBuilder.getTokenText();
    }

    public @Nullable IElementType rawLookup(int steps) {
        return myBuilder.rawLookup(steps);
    }

    public @Nullable IElementType lookAhead(int steps) {
        return myBuilder.lookAhead(steps);
    }

    public @Nullable IElementType getTokenType() {
        return myBuilder.getTokenType();
    }

    public boolean isRoot() {
        return myMarkers.isEmpty();
    }

    public boolean isHold() {
        Marker marker = getLatestMarker();
        return marker != null && marker.isHold();
    }

    public ORParser<T> duplicateAtIndex(int pos) {
        if (0 <= pos) {
            Marker foundMarker = myMarkers.get(pos);
            Marker marker = Marker.precede(foundMarker, foundMarker.getCompositeType());
            marker.updateScope(foundMarker.getScopeType());
            myMarkers.add(pos + 1, marker);
        }
        return this;
    }

    public ORParser<T> markBefore(int pos, ORCompositeType compositeType) {
        if (0 <= pos) {
            Marker scope = myMarkers.get(pos);
            Marker precedeScope = Marker.precede(scope, compositeType);
            myMarkers.add(pos + 1, precedeScope);
        }
        return this;
    }

    public @Nullable Marker getPrevious() {
        if (myMarkers.size() > 1) {
            return myMarkers.get(1);
        }
        return null;
    }

    @Deprecated
    public @NotNull ORParser<T> rollbackToPos(int pos) {
        for (int i = 0; i < pos; i++) {
            myMarkers.pop();
        }

        myMarkers.pop().rollbackTo();
        if (myVerbose) {
            System.out.println("rollbacked to: " + myBuilder.getCurrentOffset() + ", " + myBuilder.getTokenType() + "(" + myBuilder.getTokenText() + ")");
        }

        dontMove = true;
        return this;
    }

    public @NotNull ORParser<T> rollbackToIndex(int index) {
        for (int i = 0; i < index; i++) {
            myMarkers.pop();
        }

        Marker foundMarker = myMarkers.pop();
        foundMarker.rollbackTo();
        if (myVerbose) {
            System.out.println("rollback to index: " + myBuilder.getCurrentOffset() + ", " + myBuilder.getTokenType() + "(" + myBuilder.getTokenText() + ")");
        }

        Marker marker = Marker.duplicate(foundMarker);
        myMarkers.push(marker);

        if (marker.hasScope()) {
            advance();
        }
        dontMove = true;

        return this;
    }

    public @NotNull ORParser<T> rollbackToFoundIndex() {
        rollbackToIndex(myIndex);
        myIndex = -1;
        return this;
    }

    public int getIndex() {
        return myIndex;
    }

    public boolean isFound(@Nullable ORCompositeType expectedType) {
        Marker scope = find(myIndex);
        return scope != null && scope.isCompositeType(expectedType);
    }

    public ORParser<T> updateScopeToken(@Nullable Marker scope, @Nullable ORTokenElementType scopeToken) {
        if (scope != null) {
            scope.updateScope(scopeToken);
        }
        return this;
    }

    public boolean isAtIndex(int index, @NotNull ORCompositeType expectedComposite) {
        Marker marker = find(index);
        return marker != null && marker.isCompositeType(expectedComposite);
    }

    public @NotNull ORParser<T> end() {
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.end();
        }
        return this;
    }

    public ORParser<T> updateCompositeAt(int pos, @NotNull ORCompositeType compositeType) {
        if (0 <= pos && pos < myMarkers.size()) {
            Marker marker = myMarkers.get(pos);
            marker.updateCompositeType(compositeType);
            if (marker.isHold()) {
                marker.resetStatus();
            }
        }
        return this;
    }


    public ORParser<T> dropLatest() {
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.drop();
        }
        return this;
    }
}
