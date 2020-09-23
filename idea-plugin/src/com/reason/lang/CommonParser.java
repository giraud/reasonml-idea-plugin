package com.reason.lang;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.*;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommonParser<T> implements PsiParser, LightPsiParser {

  protected final T m_types;

  protected CommonParser(T types) {
    m_types = types;
  }

  @Override
  @NotNull
  public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
    builder.setDebugMode(true);

    // System.out.println("start parsing ");
    // long start = System.currentTimeMillis();

    ASTNode treeBuilt;

    // try {
    parseLight(elementType, builder);
    treeBuilt = builder.getTreeBuilt();
    // }
    // finally {
    //    long end = System.currentTimeMillis();
    //    System.out.println("end parsing in " + (end - start) + "ms");
    // }

    return treeBuilt;
  }

  @Override
  public void parseLight(IElementType elementType, PsiBuilder builder) {
    builder = adapt_builder_(elementType, builder, this, null);
    PsiBuilder.Marker m = enter_section_(builder, 0, _COLLAPSE_, null);

    ParserScope fileScope = ParserScope.markRoot(builder);

    ParserState state = new ParserState(builder, fileScope);
    parseFile(builder, state);

    // if we have a scope at last position in a file, without SEMI, we need to handle it here
    if (!state.empty()) {
      state.clear();
    }

    fileScope.end();

    if (m_types instanceof ORTypes) {
      state.mark(((ORTypes) m_types).C_FAKE_MODULE).popEnd();
    }

    exit_section_(builder, 0, m, elementType, true, true, TRUE_CONDITION);
  }

  protected abstract void parseFile(PsiBuilder builder, ParserState parserState);

  protected boolean isTypeResolution(@NotNull ParserScope scope) {
    if (m_types instanceof ORTypes) {
      return scope.isCompositeEqualTo(((ORTypes) m_types).C_TYPE_DECLARATION);
    }
    return false;
  }

  protected boolean isModuleResolution(@NotNull ParserScope scope) {
    if (m_types instanceof ORTypes) {
      return scope.isCompositeEqualTo(((ORTypes) m_types).C_MODULE_DECLARATION);
    }
    return false;
  }

  protected boolean isFunctorResolution(@Nullable ParserScope scope) {
    return scope != null
        && (scope.isResolution(functorNamedEq) || scope.isResolution(functorNamedEqColon));
  }

  protected boolean isLetResolution(@NotNull ParserScope scope) {
    return scope.isResolution(letNamed) || scope.isResolution(letNamedEq);
  }
}
