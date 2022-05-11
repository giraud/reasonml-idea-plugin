package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ParserState {
    public final PsiBuilder myBuilder;
    private final LinkedList<Marker> myMarkers = new LinkedList<>();
    public boolean dontMove = false;
    private int myIndex; // found index when using in(..)/inAny(..) functions

    public IElementType previousElementType2;
    public IElementType previousElementType1;

    public ParserState(@NotNull PsiBuilder builder) {
        myBuilder = builder;
    }

    public @NotNull ParserState popEndUntilStart() {
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
            while (scope != null && scope.getScopeTokenElementType() != scopeElementType) {
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
        // skip done/drop elements
        for (Marker marker : myMarkers) {
            if (marker.isUnset()) {
                return marker.isCompositeType(composite);
            }
        }
        return false;
    }

    public boolean isDone(@Nullable ORCompositeType composite) {
        Marker marker = myMarkers.isEmpty() ? null : myMarkers.peek();
        return marker != null && marker.isDone() && marker.isCompositeType(composite);
    }

    public boolean isDropped(@Nullable ORCompositeType composite) {
        Marker marker = myMarkers.isEmpty() ? null : myMarkers.peek();
        return marker != null && marker.isDropped() && marker.isCompositeType(composite);
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
            Marker marker = myMarkers.get(i);
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

    // zzz: merge with inScopeOrAny ?
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

    public boolean isOneOf(ORCompositeType @NotNull ... composites) {
        Marker marker = getLatestMarker();
        if (marker != null) {
            for (ORCompositeType composite : composites) {
                if (marker.isCompositeType(composite)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isScopeTokenElementType(@Nullable ORTokenElementType scopeTokenElementType) {
        Marker latestScope = getLatestMarker();
        return latestScope != null && latestScope.isScopeToken(scopeTokenElementType);
    }

    public @NotNull ParserState mark(@NotNull ORCompositeType composite) {
        Marker mark = Marker.mark(myBuilder, composite);
        myMarkers.push(mark);
        return this;
    }

    public @NotNull ParserState markScope(@NotNull ORCompositeType composite, @NotNull ORTokenElementType scope) {
        mark(composite).updateScopeToken(scope);
        return this;
    }

    public @NotNull ParserState markDummyScope(@NotNull ORCompositeType composite, @NotNull ORTokenElementType scope) {
        markScope(composite, scope);
        Marker latestScope = getLatestMarker();
        if (latestScope != null) {
            latestScope.drop();
        }
        return this;
    }

    boolean empty() {
        return myMarkers.isEmpty();
    }

    public @Nullable Marker tryPop(@NotNull LinkedList<Marker> scopes) {
        return empty() ? null : scopes.pop();
    }

    void clear() {
        Marker scope = tryPop(myMarkers);
        while (scope != null) {
            scope.end();
            scope = tryPop(myMarkers);
        }
    }

    @Nullable
    public Marker pop() {
        return tryPop(myMarkers);
    }

    public @NotNull ParserState popEnd() {
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
            while (marker != null && !ArrayUtil.contains(marker.getScopeTokenElementType(), scopeElementTypes)) {
                popEnd();
                marker = getLatestMarker();
            }
        }

        return marker;
    }

    @NotNull
    public ParserState popEndUntil(@NotNull ORCompositeType composite) {
        Marker marker = getLatestMarker();
        while (marker != null && !marker.isCompositeType(composite)) {
            popEnd();
            marker = getLatestMarker();
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
        int index = myIndex;
        myIndex = -1;
        return popEndUntilIndex(index);
    }

    public boolean hasScopeToken() {
        Marker marker = getLatestMarker();
        return marker != null && marker.hasScope();
    }

    public @NotNull ParserState updateComposite(@NotNull ORCompositeType compositeElementType) {
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.updateCompositeType(compositeElementType);
        }
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
            Marker marker = getLatestMarker();
            if (marker != null) {
                marker.setScopeType(token);
            }
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
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.setIsStart(true);
        }
    }

    public @NotNull ParserState setStart(boolean isStart) {
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.setIsStart(isStart);
        }
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

    public boolean isRoot() {
        return myMarkers.isEmpty();
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
            Marker scope = myMarkers.get(pos);
            Marker precedeScope = Marker.precedeScope(scope, compositeType);
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
        Marker scope = find(myIndex);
        return scope != null && scope.isCompositeType(expectedType);
    }

    public ParserState updateScopeToken(@Nullable Marker scope, @Nullable ORTokenElementType scopeToken) {
        if (scope != null) {
            scope.updateScope(scopeToken);
        }
        return this;
    }

    public boolean isAtIndex(int index, @NotNull ORCompositeType expectedComposite) {
        Marker marker = find(index);
        return marker != null && marker.isCompositeType(expectedComposite);
    }

    public @NotNull ParserState end() {
        Marker marker = getLatestMarker();
        if (marker != null) {
            marker.end();
        }
        return this;
    }
}
