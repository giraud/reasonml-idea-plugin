package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTokenElementType;
import java.util.LinkedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParserState {

  public final PsiBuilder m_builder;
  private final ParserScope m_rootComposite;
  private final LinkedList<ParserScope> m_composites = new LinkedList<>();

  private ParserScope m_currentScope;
  @Nullable public IElementType previousElementType2;
  @Nullable public IElementType previousElementType1;

  public boolean dontMove = false;

  public ParserState(PsiBuilder builder, ParserScope rootCompositeMarker) {
    m_builder = builder;
    m_rootComposite = rootCompositeMarker;
    m_currentScope = rootCompositeMarker;
  }

  public void popEndUntilStart() {
    ParserScope latestKnownScope;

    if (!m_composites.isEmpty()) {
      latestKnownScope = m_composites.peek();
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
  }

  @NotNull
  public ParserScope popEndUntilScope() {
    ParserScope latestKnownScope = null;

    if (!m_composites.isEmpty()) {
      latestKnownScope = m_composites.peek();
      ParserScope marker = latestKnownScope;
      while (marker != null && !marker.isScope()) {
        marker = pop();
        if (marker != null) {
          if (marker.isEmpty()) {
            marker.drop();
          } else {
            marker.end();
          }
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

    if (!m_composites.isEmpty()) {
      scope = m_composites.peek();
      while (scope != null && scope.getScopeTokenElementType() != scopeElementType) {
        popEnd();
        scope = getLatestScope();
      }
    }

    return scope;
  }

  @Nullable
  public ParserScope getLatestScope() {
    return m_composites.isEmpty() ? null : m_composites.peek();
  }

  public boolean is(ORCompositeType composite) {
    return m_currentScope.isCompositeEqualTo(composite);
  }

  public boolean isPrevious(ORCompositeType composite) {
    if (m_composites.size() >= 2) {
      return m_composites.get(1).isCompositeType(composite);
    }
    return false;
  }

  public boolean in(ORCompositeType composite) {
    for (ParserScope scope : m_composites) {
      if (scope.isCompositeEqualTo(composite)) {
        return true;
      }
    }
    return false;
  }

  public boolean inAny(ORCompositeType... composite) {
    for (ParserScope scope : m_composites) {
      if (scope.isCompositeIn(composite)) {
        return true;
      }
    }
    return false;
  }

  public boolean isCurrentCompositeElementType(ORCompositeType compositeType) {
    return is(compositeType);
  }

  public boolean isScopeTokenElementType(ORTokenElementType scopeTokenElementType) {
    return m_currentScope.isScopeTokenEqualTo(scopeTokenElementType);
  }

  public boolean isCurrentResolution(ParserScopeEnum scope) {
    return m_currentScope.isResolution(scope);
  }

  public boolean isPreviousResolution(ParserScopeEnum scope) {
    if (m_composites.size() >= 2) {
      return m_composites.get(1).isResolution(scope);
    }
    return false;
  }

  public boolean isGrandPreviousResolution(ParserScopeEnum scope) {
    if (m_composites.size() >= 3) {
      return m_composites.get(2).isResolution(scope);
    }
    return false;
  }

  public boolean isGrandParent(ORCompositeType composite) {
    if (m_composites.size() >= 3) {
      return m_composites.get(2).isCompositeType(composite);
    }
    return false;
  }

  @NotNull
  public ParserState complete() {
    m_currentScope.complete();
    return this;
  }

  @NotNull
  public ParserState add(@NotNull ParserScope scope) {
    m_composites.push(scope);
    m_currentScope = scope;
    return this;
  }

  // is resolution needed ?
  public ParserState markScope(ORCompositeType compositeType, ORTokenElementType scopeType) {
    add(ParserScope.markScope(m_builder, compositeType, scopeType));
    m_currentScope.complete();
    return this;
  }

  public ParserState markOptional(ORCompositeType compositeElementType) {
    add(ParserScope.mark(m_builder, compositeElementType));
    return this;
  }

  public ParserState mark(ORCompositeType compositeType) {
    add(ParserScope.mark(m_builder, compositeType));
    m_currentScope.complete();
    return this;
  }

  boolean empty() {
    return m_composites.isEmpty();
  }

  public ParserScope tryPop(LinkedList<ParserScope> scopes) {
    return empty() ? null : scopes.pop();
  }

  void clear() {
    ParserScope scope = tryPop(m_composites);
    while (scope != null) {
      scope.end();
      scope = tryPop(m_composites);
    }
    m_currentScope = m_rootComposite;
  }

  private void updateCurrentScope() {
    m_currentScope = m_composites.isEmpty() ? m_rootComposite : m_composites.peek();
  }

  @Nullable
  public ParserScope pop() {
    ParserScope scope = tryPop(m_composites);
    updateCurrentScope();
    return scope;
  }

  @NotNull
  public ParserState popEnd() {
    ParserScope scope = pop();
    if (scope != null) {
      scope.end();
    }
    return this;
  }

  public ParserState popCancel() {
    ParserScope scope = pop();
    if (scope != null) {
      scope.drop();
    }
    return this;
  }

  @Nullable
  public ParserScope popEndUntilOneOfElementType(@NotNull ORTokenElementType... scopeElementTypes) {
    ParserScope scope = null;

    if (!m_composites.isEmpty()) {
      scope = m_composites.peek();
      while (scope != null
          && !ArrayUtil.contains(scope.getScopeTokenElementType(), scopeElementTypes)) {
        popEnd();
        scope = getLatestScope();
      }
    }

    return scope;
  }

  @Nullable
  public ParserScope popEndUntilOneOfResolution(@NotNull ParserScopeEnum... resolutions) {
    ParserScope scope = null;

    if (!m_composites.isEmpty()) {
      scope = m_composites.peek();
      while (scope != null && !ArrayUtil.contains(scope.getResolution(), resolutions)) {
        popEnd();
        scope = getLatestScope();
      }
    }

    return scope;
  }

  @NotNull
  public ParserState popEndUntil(@NotNull ORCompositeType composite) {
    if (!m_composites.isEmpty()) {
      ParserScope scope = m_composites.peek();
      while (scope != null && !scope.isCompositeType(composite)) {
        popEnd();
        scope = getLatestScope();
      }
    }

    return this;
  }

  @NotNull
  public ParserState popEndUntilResolution(@NotNull ParserScopeEnum resolution) {
    if (!m_composites.isEmpty()) {
      ParserScope scope = m_composites.peek();
      while (scope != null && !scope.isResolution(resolution)) {
        popEnd();
        scope = getLatestScope();
      }
    }

    return this;
  }

  @NotNull
  public ParserState resolution(@NotNull ParserScopeEnum resolution) {
    m_currentScope.resolution(resolution);
    return this;
  }

  public boolean notInScopeExpression() {
    return !m_currentScope.isScope();
  }

  public boolean isInScopeExpression() {
    return m_currentScope.isScope();
  }

  @NotNull
  public ParserState updateCurrentCompositeElementType(
      @NotNull ORCompositeType compositeElementType) {
    m_currentScope.updateCompositeElementType(compositeElementType);
    return this;
  }

  public ParserScopeEnum currentResolution() {
    return m_currentScope.getResolution();
  }

  @NotNull
  public ParserState advance() {
    previousElementType2 = previousElementType1;
    previousElementType1 = m_builder.getTokenType();
    m_builder.advanceLexer();
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
      IElementType elementType = (IElementType) compositeType;
      PsiBuilder.Marker mark = m_builder.mark();
      advance();
      mark.done(elementType);
    }
    return this;
  }

  @NotNull
  public ParserState setStart() {
    m_currentScope.setIsStart(true);
    return this;
  }

  @NotNull
  public ParserState setStart(boolean isStart) {
    m_currentScope.setIsStart(isStart);
    return this;
  }

  public void error(String message) {
    m_builder.error(message);
  }

  public ParserState remapCurrentToken(ORTokenElementType elementType) {
    m_builder.remapCurrentToken(elementType);
    return this;
  }

  public void setWhitespaceSkippedCallback(@Nullable WhitespaceSkippedCallback callback) {
    m_builder.setWhitespaceSkippedCallback(callback);
  }

  public String getTokenText() {
    return m_builder.getTokenText();
  }

  public IElementType rawLookup(int steps) {
    return m_builder.rawLookup(steps);
  }

  public IElementType lookAhead(int steps) {
    return m_builder.lookAhead(steps);
  }

  public IElementType getTokenType() {
    return m_builder.getTokenType();
  }

  public ParserState dummy() {
    m_currentScope.dummy();
    return this;
  }

  public boolean isInContext(ParserScopeEnum resolution) {
    for (ParserScope composite : m_composites) {
      if (composite.isResolution(resolution)) {
        return true;
      }
    }
    return false;
  }
}
