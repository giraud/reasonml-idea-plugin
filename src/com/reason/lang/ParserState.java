package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ParserState {
    public final PsiBuilder myBuilder;
    private final LinkedList<MarkerScope> myMarkers = new LinkedList<>();
    private final MarkerScope myRootMarker;
    public boolean dontMove = false;
    private int myIndex; // found index when using in(..)/inAny(..) functions

    // zzz ?
    public IElementType previousElementType2;
    public IElementType previousElementType1;
    // zzz

    public ParserState(@NotNull PsiBuilder builder, @NotNull MarkerScope rootCompositeMarker) {
        myBuilder = builder;
        myRootMarker = rootCompositeMarker;
    }

    public @NotNull ParserState popEndUntilStart() {
        MarkerScope latestKnownScope;

        if (!myMarkers.isEmpty()) {
            latestKnownScope = myMarkers.peek();
            MarkerScope scope = latestKnownScope;
            while (scope != null && !scope.isStart()) {
                scope = pop();
                if (scope != null) {
                    if (scope.isEmpty()) {
                        scope.drop();
                    } else {
                        scope.end();
                    }
                }
                scope = getLatestScope();
            }
        }

        return this;
    }

    public @NotNull MarkerScope popEndUntilScope() {
        MarkerScope latestKnownScope = null;

        if (!myMarkers.isEmpty()) {
            latestKnownScope = myMarkers.peek();
            MarkerScope marker = latestKnownScope;
            while (marker != myRootMarker && !marker.hasScope()) {
                marker = pop();
                if (marker != null) {
                    //if (marker.isEmpty()) {
                    //    marker.drop();
                    //} else {
                    marker.end();
                    //}
                    latestKnownScope = marker;
                }
                marker = getLatestScope();
            }
        }

        return latestKnownScope == null ? myRootMarker : latestKnownScope;
    }

    public @Nullable MarkerScope popEndUntilScopeToken(@NotNull ORTokenElementType scopeElementType) {
        MarkerScope scope = null;

        if (!myMarkers.isEmpty()) {
            scope = myMarkers.peek();
            while (scope != myRootMarker && scope.getScopeTokenElementType() != scopeElementType) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public MarkerScope peekUntilScopeToken(@NotNull ORTokenElementType scopeElementType) {
        if (!myMarkers.isEmpty()) {
            for (MarkerScope scope : myMarkers) {
                if (scope != null && scope.getScopeTokenElementType() == scopeElementType) {
                    return scope;
                }
            }
        }

        return null;
    }

    public @NotNull MarkerScope getLatestScope() {
        return myMarkers.isEmpty() ? myRootMarker : myMarkers.peek();
    }

    public @Nullable MarkerScope getLatestCompletedScope() {
        //ParserScope completed = m_latestCompleted;
        //m_latestCompleted = null;
        //return completed;
        return null;
    }

    public boolean is(@Nullable ORCompositeType composite) {
        return !myMarkers.isEmpty() && myMarkers.peek().isCompositeType(composite);
    }

    public boolean isDone(@Nullable ORCompositeType composite) {
        MarkerScope marker = myMarkers.isEmpty() ? null : myMarkers.peek();
        return marker != null && marker.isDone() && marker.isCompositeType(composite);
    }

    public boolean isParent(ORCompositeType composite) {
        boolean found = false;
        if (myMarkers.size() >= 2) {
            // current index is 0
            found = myMarkers.get(1).isCompositeType(composite);
        }
        myIndex = found ? 1 : -1;
        return found;
    }

    public boolean isPrevious(ORCompositeType expectedComposite, int index) {
        int size = myMarkers.size() - 1;
        if (index >= 0 && index < size) {
            return myMarkers.get(index + 1).isCompositeType(expectedComposite);
        }
        return false;
    }

    public boolean isGrandParent(ORCompositeType composite) {
        if (myMarkers.size() >= 3) {
            return myMarkers.get(2).isCompositeType(composite);
        }
        return false;
    }

    public boolean in(ORCompositeType composite) {
        return in(composite, null, myMarkers.size(), false);
    }

    public boolean inScopeOr(ORCompositeType composite) {
        return in(composite, null, myMarkers.size(), true);
    }

    public boolean in(ORCompositeType composite, @Nullable ORCompositeType excluded) {
        return in(composite, excluded, myMarkers.size(), false);
    }

    public boolean in(ORCompositeType composite, @Nullable ORCompositeType excluded, int maxDepth, boolean useScope) {
        int size = myMarkers.size();
        int stop = Math.min(size, maxDepth);
        for (int i = 0; i < stop; i++) {
            MarkerScope marker = myMarkers.get(i);
            if (marker.isUnset()) { // not dropped or done
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
        int size = myMarkers.size();
        int stop = Math.min(size, myMarkers.size());
        for (int i = 0; i < stop; i++) {
            MarkerScope marker = myMarkers.get(i);
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
        int size = myMarkers.size();
        int stop = Math.min(size, myMarkers.size());
        for (int i = 0; i < stop; i++) {
            MarkerScope marker = myMarkers.get(i);
            if (marker.isUnset()) { // not dropped or done
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

    public @Nullable MarkerScope find(int index) {
        return (0 <= index && index < myMarkers.size()) ? myMarkers.get(index) : null;
    }

    public int indexOfComposite(@NotNull ORCompositeType composite) {
        for (int i = myMarkers.size() - 1; i >= 0; i--) {
            MarkerScope markerScope = myMarkers.get(i);
            if (markerScope.isCompositeType(composite)) {
                return i;
            }
        }
        return -1;
    }

    // zzz: merge with inScopeOrAny ?
    public boolean inAny(ORCompositeType... composite) {
        int stop = myMarkers.size();
        for (int i = 0; i < stop; i++) {
            MarkerScope markerScope = myMarkers.get(i);
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
            MarkerScope markerScope = myMarkers.get(i);
            if (markerScope.hasScope() || markerScope.isCompositeIn(composite)) {
                myIndex = i;
                return true;
            }
        }
        myIndex = -1;
        return false;
    }

    public boolean isOneOf(ORCompositeType @NotNull ... composites) {
        MarkerScope marker = getLatestScope();
        for (ORCompositeType composite : composites) {
            if (marker.isCompositeType(composite)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentCompositeElementType(@Nullable ORCompositeType compositeType) {
        return is(compositeType);
    }

    public boolean isScopeTokenElementType(@Nullable ORTokenElementType scopeTokenElementType) {
        return getLatestScope().isScopeToken(scopeTokenElementType);
    }

    public @NotNull ParserState mark(@NotNull ORCompositeType composite) {
        myMarkers.push(MarkerScope.mark(myBuilder, composite));
        return this;
    }

    public @NotNull ParserState markScope(@NotNull ORCompositeType composite, @NotNull ORTokenElementType scope) {
        mark(composite).updateScopeToken(scope);
        return this;
    }

    public @NotNull ParserState markDummyScope(@NotNull ORCompositeType composite, @NotNull ORTokenElementType scope) {
        markScope(composite, scope);
        getLatestScope().drop();
        return this;
    }

    boolean empty() {
        return myMarkers.isEmpty();
    }

    public @Nullable MarkerScope tryPop(@NotNull LinkedList<MarkerScope> scopes) {
        return empty() ? null : scopes.pop();
    }

    void clear() {
        MarkerScope scope = tryPop(myMarkers);
        while (scope != null) {
            scope.end();
            scope = tryPop(myMarkers);
        }
    }

    @Nullable
    public MarkerScope pop() {
        return tryPop(myMarkers);
    }

    public @NotNull ParserState popEnd() {
        MarkerScope scope = pop();
        if (scope != null) {
            scope.end();
        }
        return this;
    }

    // skip all optional intermediate scopes, and pop latest complete
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull ParserState popEndComplete() {
        MarkerScope scope = pop();
        while (scope != null) {
            scope.drop();
            scope = pop();
        }
        return this;
    }

    @Nullable
    public MarkerScope popEndUntilOneOf(@NotNull ORCompositeType... composites) {
        MarkerScope scope = null;

        if (!myMarkers.isEmpty()) {
            scope = myMarkers.peek();
            while (scope != null && !ArrayUtil.contains(scope.getCompositeType(), composites)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public MarkerScope popEndUntilOneOfElementType(@NotNull ORTokenElementType... scopeElementTypes) {
        MarkerScope scope = null;

        if (!myMarkers.isEmpty()) {
            scope = myMarkers.peek();
            while (scope != myRootMarker && !ArrayUtil.contains(scope.getScopeTokenElementType(), scopeElementTypes)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @NotNull
    public ParserState popEndUntil(@NotNull ORCompositeType composite) {
        if (!myMarkers.isEmpty()) {
            MarkerScope scope = myMarkers.peek();
            while (scope != myRootMarker && !scope.isCompositeType(composite)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return this;
    }

    public ParserState popEndUntilIndex(int index) {
        if (0 <= index && index <= myMarkers.size()) {
            for (int i = 0; i < index; i++) {
                popEnd();
            }
        }
        return this;
    }

    public ParserState popEndUntilFoundIndex() {
        return popEndUntilIndex(getIndex());
    }

    public boolean hasScopeToken() {
        return getLatestScope().hasScope();
    }

    public @NotNull ParserState updateCurrentCompositeElementType(@NotNull ORCompositeType compositeElementType) {
        getLatestScope().updateCompositeElementType(compositeElementType);
        return this;
    }

    public @NotNull ParserState advance() {
        previousElementType2 = previousElementType1;
        previousElementType1 = myBuilder.getTokenType();
        myBuilder.advanceLexer();
        dontMove = true;
        return this;
    }

    public @NotNull ParserState updateScopeToken(@Nullable ORTokenElementType token) {
        if (token != null) {
            getLatestScope().setScopeTokenType(token);
        }
        return this;
    }

    public @NotNull ParserState wrapWith(@NotNull ORCompositeType compositeType) {
        if (compositeType instanceof IElementType) {
            mark(compositeType).advance();
            myMarkers.getFirst().end();
        }
        return this;
    }

    public void setStart() {
        getLatestScope().setIsStart(true);
    }

    public @NotNull ParserState setStart(boolean isStart) {
        getLatestScope().setIsStart(isStart);
        return this;
    }

    public void error(@NotNull String message) {
        myBuilder.error(message);
    }

    public @NotNull ParserState remapCurrentToken(ORTokenElementType elementType) {
        myBuilder.remapCurrentToken(elementType);
        return this;
    }

    public @NotNull ParserState setWhitespaceSkippedCallback(@Nullable WhitespaceSkippedCallback callback) {
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

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull ParserState updatePreviousComposite(@NotNull ORCompositeType composite) {
        if (myMarkers.size() > 1) {
            myMarkers.get(1).updateCompositeElementType(composite);
        }
        return this;
    }

    public boolean isRoot() {
        return getLatestScope() == myRootMarker;
    }

    public boolean isRoot(@Nullable MarkerScope marker) {
        return marker == myRootMarker;
    }

    public boolean isOptional() {
        return false;
    }

    public @NotNull ParserState markOptionalParenDummyScope(@NotNull ORTypes types, @NotNull ORCompositeType scopeType) {
        if (getTokenType() == types.LPAREN) {
            markScope(scopeType, types.LPAREN).advance();
        }
        return this;
    }

    public @NotNull ParserState markOptionalParenDummyScope(@NotNull ORTypes types) {
        return markOptionalParenDummyScope(types, types.C_DUMMY);
    }

    public boolean isDummy() {
        return false;
    }

    public ParserState markBefore(int pos, ORCompositeType compositeType) {
        if (pos >= 0) {
            MarkerScope scope = myMarkers.get(pos);
            MarkerScope precedeScope = MarkerScope.precedeScope(scope, compositeType);
            myMarkers.add(pos + 1, precedeScope);
        }
        return this;
    }

    public @Nullable MarkerScope getPrevious() {
        if (myMarkers.size() > 1) {
            return myMarkers.get(1);
        }
        return null;
    }

    public @NotNull ParserState rollbackTo(int pos) {
        for (int i = 0; i < pos; i++) {
            myMarkers.pop();
        }
        myMarkers.pop().rollbackTo();
        dontMove = true;
        return this;
    }

    public int getIndex() {
        return myIndex;
    }

    public boolean isFound(@Nullable ORCompositeType expectedType) {
        MarkerScope scope = find(myIndex);
        return scope != null && scope.isCompositeType(expectedType);
    }

    public ParserState updateScopeToken(@Nullable MarkerScope scope, @Nullable ORTokenElementType scopeToken) {
        if (scope != null) {
            scope.updateScope(scopeToken);
        }
        return this;
    }

    public boolean isAtIndex(int index, @NotNull ORCompositeType expectedComposite) {
        MarkerScope marker = find(index);
        return marker != null && marker.isCompositeType(expectedComposite);
    }

    public @NotNull ParserState end() {
        getLatestScope().end();
        return this;
    }
}
