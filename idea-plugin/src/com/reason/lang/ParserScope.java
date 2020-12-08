package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTokenElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParserScope {

  @NotNull private final PsiBuilder m_builder;
  private final int m_offset;

  private @Nullable ParserScopeEnum m_resolution;
  private ORCompositeType m_compositeElementType;
  private ORTokenElementType m_scopeTokenElementType;
  private boolean m_isComplete = true;
  private boolean m_isDummy = false; // Always drop
  private boolean m_isStart = false;
  public PsiBuilder.Marker m_mark;

  private ParserScope(
      @NotNull PsiBuilder builder,
      @NotNull PsiBuilder.Marker mark,
      ORCompositeType compositeElementType,
      ORTokenElementType scopeTokenElementType) {
    m_builder = builder;
    m_mark = mark;
    m_offset = builder.getCurrentOffset();
    m_compositeElementType = compositeElementType;
    m_scopeTokenElementType = scopeTokenElementType;
  }

  @NotNull
  public static ParserScope mark(
      @NotNull PsiBuilder builder, @NotNull ORCompositeType compositeElementType) {
    return new ParserScope(builder, builder.mark(), compositeElementType, null);
  }

  @NotNull
  public static ParserScope markScope(
      @NotNull PsiBuilder builder,
      @NotNull ORCompositeType compositeElementType,
      @NotNull ORTokenElementType scopeTokenElementType) {
    return new ParserScope(builder, builder.mark(), compositeElementType, scopeTokenElementType);
  }

  public static ParserScope precedeScope(ParserScope scope, ORCompositeType compositeType) {
    PsiBuilder.Marker precede = scope.m_mark.precede();
    return new ParserScope(scope.m_builder, precede, compositeType, null);
  }

  public static ParserScope precedeMark(
      PsiBuilder builder, PsiBuilder.Marker mark, ORCompositeType compositeType) {
    PsiBuilder.Marker precede = mark.precede();
    return new ParserScope(builder, precede, compositeType, null);
  }

  @NotNull
  static ParserScope markRoot(@NotNull PsiBuilder builder) {
    return new ParserScope(builder, builder.mark(), null, null);
  }

  public int getLength() {
    return m_builder.getCurrentOffset() - m_offset;
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
    if (m_mark != null) {
      if (m_compositeElementType instanceof IElementType) {
        m_mark.done((IElementType) m_compositeElementType);
      } else {
        m_mark.drop();
      }
      m_mark = null;
    }
  }

  void drop() {
    if (m_mark != null) {
      m_mark.drop();
      m_mark = null;
    }
  }

  public ParserScope complete() {
    m_isComplete = true;
    return this;
  }

  public ParserScope optional() {
    m_isComplete = false;
    return this;
  }

  public boolean isOptional() {
    return !m_isComplete;
  }

  public void dummy() {
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

  boolean isCompositeEqualTo(ORCompositeType compositeType) {
    return m_compositeElementType == compositeType;
  }

  boolean isCompositeIn(ORCompositeType @NotNull ... compositeType) {
    for (ORCompositeType composite : compositeType) {
      if (m_compositeElementType == composite) return true;
    }
    return false;
  }

  public boolean isScopeToken(ORTokenElementType tokenElementType) {
    return m_scopeTokenElementType == tokenElementType;
  }

  void setScopeTokenType(@NotNull ORTokenElementType tokenElementType) {
    m_scopeTokenElementType = tokenElementType;
  }

  public void updateCompositeElementType(ORCompositeType compositeType) {
    m_compositeElementType = compositeType;
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
    return m_scopeTokenElementType != null;
  }

  ORTokenElementType getScopeTokenElementType() {
    return m_scopeTokenElementType;
  }

  public void rollbackTo() {
    if (m_mark != null) {
      m_mark.rollbackTo();
    }
  }

  public boolean isCompositeType(ORCompositeType elementType) {
    return m_compositeElementType == elementType;
  }

  public ORCompositeType getCompositeType() {
    return m_compositeElementType;
  }

  public ORTokenElementType getScopeType() {
    return m_scopeTokenElementType;
  }
}
