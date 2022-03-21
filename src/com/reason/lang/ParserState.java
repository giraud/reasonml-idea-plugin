package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ParserState {
    private final PsiBuilder myBuilder;
    private final LinkedList<ParserScope> myMarkers = new LinkedList<>();
    public boolean dontMove = false;
    private int myIndex; // found index when using in() function

    // zzz ?
    public IElementType previousElementType2;
    public IElementType previousElementType1;
    private final ParserScope m_rootComposite;
    private ParserScope m_currentScope;
    private ParserScope m_latestCompleted;
    // zzz

    public ParserState(PsiBuilder builder, ParserScope rootCompositeMarker) {
        myBuilder = builder;
        m_rootComposite = rootCompositeMarker;
        m_currentScope = rootCompositeMarker;
    }

    public ParserState popEndUntilStart() {
        ParserScope latestKnownScope;

        if (!myMarkers.isEmpty()) {
            latestKnownScope = myMarkers.peek();
            ParserScope scope = latestKnownScope;
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

    @NotNull
    public ParserScope popEndUntilScope() {
        ParserScope latestKnownScope = null;

        if (!myMarkers.isEmpty()) {
            latestKnownScope = myMarkers.peek();
            ParserScope marker = latestKnownScope;
            while (marker != null && !marker.hasScope()) {
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

        return latestKnownScope == null ? m_rootComposite : latestKnownScope;
    }

    @Nullable
    public ParserScope popEndUntilScopeToken(@NotNull ORTokenElementType scopeElementType) {
        ParserScope scope = null;

        if (!myMarkers.isEmpty()) {
            scope = myMarkers.peek();
            while (scope != null && scope.getScopeTokenElementType() != scopeElementType) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope peekUntilScopeToken(@NotNull ORTokenElementType scopeElementType) {
        if (!myMarkers.isEmpty()) {
            for (ParserScope scope : myMarkers) {
                if (scope != null && scope.getScopeTokenElementType() == scopeElementType) {
                    return scope;
                }
            }
        }

        return null;
    }

    public @Nullable ParserScope getLatestScope() {
        return myMarkers.isEmpty() ? null : myMarkers.peek();
    }

    public @Nullable ParserScope getLatestCompletedScope() {
        ParserScope completed = m_latestCompleted;
        m_latestCompleted = null;
        return completed;
    }

    public boolean is(ORCompositeType composite) {
        return m_currentScope.isCompositeType(composite);
    }

    public boolean isComplete(@NotNull ORCompositeType expectedComposite) {
        // find first NON-optional composite
        for (ParserScope composite : myMarkers) {
            if (!composite.isOptional()) {
                return composite.isCompositeType(expectedComposite);
            }
        }
        return false;
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
        return in(composite, null, myMarkers.size());
    }

    public boolean in(ORCompositeType composite, @Nullable ORCompositeType excluded) {
        return in(composite, excluded, myMarkers.size());
    }

    public boolean in(ORCompositeType composite, @Nullable ORCompositeType excluded, int maxDepth) {
        int size = myMarkers.size();
        int stop = Math.min(size, maxDepth);
        for (int i = 0; i < stop; i++) {
            ParserScope markerScope = myMarkers.get(i);
            if (markerScope.isCompositeType(excluded)) {
                myIndex = -1;
                return false;
            }
            if (markerScope.isCompositeType(composite)) {
                myIndex = i;
                return true;
            }
        }
        myIndex = -1;
        return false;
    }

    public @Nullable ParserScope find(int index) {
        return myMarkers.get(index);
    }

    public @Nullable ParserScope findNext(int bindingPos) {
        return bindingPos > 0 ? myMarkers.get(bindingPos - 1) : null;
    }

    public int indexOfComposite(@NotNull ORCompositeType composite) {
        for (int i = myMarkers.size() - 1; i >= 0; i--) {
            ParserScope markerScope = myMarkers.get(i);
            if (markerScope.isCompositeType(composite)) {
                return i;
            }
        }
        return -1;
    }

    public boolean inAny(ORCompositeType... composite) {
        int stop = myMarkers.size();
        for (int i = 0; i < stop; i++) {
            ParserScope markerScope = myMarkers.get(i);
            if (markerScope.isCompositeIn(composite)) {
                myIndex = i;
                return true;
            }
        }
        myIndex = -1;
        return false;
    }

    public int inAny2(ORCompositeType... composites) { // zzz delete
        for (int i = myMarkers.size() - 1; i >= 0; i--) {
            ParserScope markerScope = myMarkers.get(i);
            if (markerScope.isCompositeIn(composites)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isOneOf(ORCompositeType @NotNull ... composites) {
        for (ORCompositeType composite : composites) {
            if (m_currentScope.isCompositeType(composite)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentCompositeElementType(ORCompositeType compositeType) {
        return is(compositeType);
    }

    public boolean isScopeTokenElementType(ORTokenElementType scopeTokenElementType) {
        return m_currentScope.isScopeToken(scopeTokenElementType);
    }

    public boolean isCurrentResolution(ParserScopeEnum scope) {
        return m_currentScope.isResolution(scope);
    }

    public boolean isPreviousResolution(ParserScopeEnum scope) {
        if (myMarkers.size() >= 2) {
            return myMarkers.get(1).isResolution(scope);
        }
        return false;
    }

    public boolean isGrandPreviousResolution(ParserScopeEnum scope) {
        if (myMarkers.size() >= 3) {
            return myMarkers.get(2).isResolution(scope);
        }
        return false;
    }

    @NotNull
    public ParserState complete() {
        m_currentScope.complete();
        return this;
    }

    @NotNull
    public ParserState completePrevious() {
        if (myMarkers.size() >= 2) {
            myMarkers.get(1).complete();
        }
        return this;
    }

    @NotNull
    public ParserState optional() {
        m_currentScope.optional();
        return this;
    }

    public void add(@NotNull ParserScope scope) {
        myMarkers.push(scope);
        m_currentScope = scope;
    }

    public @NotNull ParserState markDummy(@NotNull ORTypes types) {
        mark(types.C_DUMMY).dummy();
        return this;
    }

    public @NotNull ParserState mark(@NotNull ORCompositeType composite) {
        ParserScope scope = ParserScope.mark(myBuilder, composite);
        add(scope);
        //m_latestMark = scope.myMark;
        return this;
    }

    public @NotNull ParserState markScope(@NotNull ORCompositeType composite, @NotNull ORTokenElementType scope) {
        ParserState state = mark(composite);
        state.updateScopeToken(scope);
        return this;
    }

    public @NotNull ParserState markOptional(@NotNull ORCompositeType composite) {
        ParserState state = mark(composite);
        state.optional();
        return this;
    }

    //public @NotNull ParserState precedeScope(@NotNull ORCompositeType composite) {
    //    ParserScope latest = pop();
    //    if (latest != null) {
    //        add(ParserScope.precedeScope(latest, composite));
    //        add(latest);
    //    }
    //    return this;
    //}

    boolean empty() {
        return myMarkers.isEmpty();
    }

    public @Nullable ParserScope tryPop(@NotNull LinkedList<ParserScope> scopes) {
        return empty() ? null : scopes.pop();
    }

    void clear() {
        ParserScope scope = tryPop(myMarkers);
        while (scope != null) {
            scope.end();
            scope = tryPop(myMarkers);
        }
        m_currentScope = m_rootComposite;
    }

    private void updateCurrentScope() {
        m_currentScope = myMarkers.isEmpty() ? m_rootComposite : myMarkers.peek();
    }

    @Nullable
    public ParserScope pop() {
        ParserScope scope = tryPop(myMarkers);
        updateCurrentScope();
        return scope;
    }

    public @NotNull ParserState popEnd() {
        ParserScope scope = pop();
        if (scope != null) {
            scope.end();
        }
        return this;
    }

    // skip all optional intermediate scopes, and pop latest complete
    public @NotNull ParserState popEndComplete() {
        ParserScope scope = pop();
        while (scope != null && scope.isOptional()) {
            scope.drop();
            scope = pop();
        }
        if (scope != null) {
            scope.end();
            m_latestCompleted = scope;
        }
        return this;
    }

    //
    public @NotNull ParserState dropOptionals() {
        while (!myMarkers.isEmpty() && m_currentScope.isOptional()) {
            popCancel();
        }
        return this;
    }

    public @NotNull ParserState popCancel() {
        ParserScope scope = pop();
        if (scope != null) {
            scope.drop();
        }
        return this;
    }

    public void popEndUntilOneOf(@NotNull ORCompositeType... composites) {
        if (!myMarkers.isEmpty()) {
            ParserScope scope = myMarkers.peek();
            while (scope != null && !ArrayUtil.contains(scope.getCompositeType(), composites)) {
                popEnd();
                scope = getLatestScope();
            }
        }
    }

    @Nullable
    public ParserScope popEndUntilOneOfElementType(@NotNull ORTokenElementType... scopeElementTypes) {
        ParserScope scope = null;

        if (!myMarkers.isEmpty()) {
            scope = myMarkers.peek();
            while (scope != null
                    && !ArrayUtil.contains(scope.getScopeTokenElementType(), scopeElementTypes)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    public void popEndUntilOneOfResolution(@NotNull ParserScopeEnum... resolutions) {
        if (!myMarkers.isEmpty()) {
            ParserScope scope = myMarkers.peek();
            while (scope != null && !ArrayUtil.contains(scope.getResolution(), resolutions)) {
                popEnd();
                scope = getLatestScope();
            }
        }
    }

    @NotNull
    public ParserState popEndUntil(@NotNull ORCompositeType composite) {
        if (!myMarkers.isEmpty()) {
            ParserScope scope = myMarkers.peek();
            while (scope != null && !scope.isCompositeType(composite)) {
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

    @NotNull
    public ParserState popEndUntilResolution(@NotNull ParserScopeEnum resolution) {
        if (!myMarkers.isEmpty()) {
            ParserScope scope = myMarkers.peek();
            while (scope != null && !scope.isResolution(resolution)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return this;
    }

    @NotNull
    public ParserState resolution(@Nullable ParserScopeEnum resolution) {
        m_currentScope.resolution(resolution);
        return this;
    }

    public boolean hasScopeToken() {
        return m_currentScope.hasScope();
    }

    public @NotNull ParserState updateCurrentCompositeElementType(@NotNull ORCompositeType compositeElementType) {
        m_currentScope.updateCompositeElementType(compositeElementType);
        return this;
    }

    @NotNull
    public ParserState advance() {
        previousElementType2 = previousElementType1;
        previousElementType1 = myBuilder.getTokenType();
        myBuilder.advanceLexer();
        dontMove = true;
        return this;
    }

    @NotNull
    public ParserState updateScopeToken(@Nullable ORTokenElementType token) {
        if (token != null) {
            m_currentScope.setScopeTokenType(token);
        }
        return this;
    }

    @NotNull
    public ParserState wrapWith(@NotNull ORCompositeType compositeType) {
        if (compositeType instanceof IElementType) {
            mark(compositeType).advance();
            myMarkers.getFirst().end();
        }
        return this;
    }

    public void setStart() {
        m_currentScope.setIsStart(true);
    }

    @NotNull
    public ParserState setStart(boolean isStart) {
        m_currentScope.setIsStart(isStart);
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

    public @NotNull ParserState dummy() {
        m_currentScope.dummy();
        return this;
    }

    public boolean isInContext(ParserScopeEnum resolution) {
        for (ParserScope composite : myMarkers) {
            if (composite.isResolution(resolution)) {
                return true;
            }
        }
        return false;
    }

    public @Nullable ParserScope findScopeContext(@NotNull ORTypes types) {
        int level = 1;
        ParserScope parserScope = myMarkers.size() < 2 ? null : myMarkers.get(level);
        while (parserScope != null && parserScope.isCompositeType(types.C_DUMMY)) {
            level++;
            parserScope = myMarkers.get(level);
        }

        return parserScope;
    }

    public @NotNull ParserState updatePreviousComposite(@NotNull ORCompositeType composite) {
        if (myMarkers.size() > 1) {
            myMarkers.get(1).updateCompositeElementType(composite);
        }
        return this;
    }

    public boolean isRoot() {
        return m_currentScope == m_rootComposite;
    }

    public boolean isOptional() {
        return m_currentScope.isOptional();
    }

    public @NotNull ParserState markOptionalParenDummyScope(@NotNull ORTypes types, @NotNull ORCompositeType scopeType) {
        if (getTokenType() == types.LPAREN) {
            markScope(scopeType, types.LPAREN).dummy().advance();
        }
        return this;
    }

    public @NotNull ParserState markOptionalParenDummyScope(@NotNull ORTypes types) {
        return markOptionalParenDummyScope(types, types.C_DUMMY);
    }

    public boolean isDummy() {
        return m_currentScope.isDummy();
    }

    public ParserState markBefore(int pos, ORCompositeType compositeType) {
        if (pos >= 0) {
            ParserScope scope = myMarkers.get(pos);
            ParserScope precedeScope = ParserScope.precedeScope(scope, compositeType);
            myMarkers.add(pos + 1, precedeScope);
        }
        return this;
    }

    public @Nullable ParserScope getPrevious() {
        if (myMarkers.size() > 1) {
            return myMarkers.get(1);
        }
        return null;
    }

    public ParserState rollbackTo(int pos) {
        for (int i = 0; i < pos; i++) {
            myMarkers.pop();
        }
        myMarkers.getFirst().rollbackTo();
        myMarkers.pop();
        return this;
    }

    public ParserState rollbackAfter(ORCompositeType compositeType) {
        int pos = indexOfComposite(compositeType);
        return rollbackTo(pos - 1);
    }

    public int getIndex() {
        return myIndex;
    }

    public boolean isLatestScopeFound(ORCompositeType expectedType) {
        ParserScope scope = find(myIndex);
        return scope != null && scope.isCompositeType(expectedType);
    }

    public ParserState updateScopeToken(ParserScope scope, ORTokenElementType scopeToken) {
        if (scope != null) {
            scope.updateScope(scopeToken);
        }
        return this;
    }
}
