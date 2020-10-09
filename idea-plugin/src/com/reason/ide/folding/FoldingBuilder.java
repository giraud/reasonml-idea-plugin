package com.reason.ide.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.ocamlyacc.OclYaccHeader;
import com.reason.lang.core.psi.ocamlyacc.OclYaccRule;
import com.reason.lang.core.psi.ocamlyacc.OclYaccRuleBody;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.ocamlyacc.OclYaccLazyTypes;
import com.reason.lang.reason.RmlTypes;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoldingBuilder extends FoldingBuilderEx {
  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(
      @NotNull PsiElement root, @NotNull Document document, boolean quick) {
    List<FoldingDescriptor> descriptors = new ArrayList<>();
    ORTypes types = ORUtil.getTypes(root.getLanguage());

    PsiTreeUtil.processElements(
        root,
        element -> {
          if (element instanceof PsiLet) {
            foldLet(descriptors, (PsiLet) element);
          } else if (element instanceof PsiType) {
            foldType(descriptors, (PsiType) element);
          } else if (element instanceof PsiInnerModule) {
            foldModule(descriptors, (PsiInnerModule) element);
          } else if (element instanceof PsiFunction) {
            foldFunction(descriptors, (PsiFunction) element);
          } else if (element instanceof PsiFunctor) {
            foldFunctor(descriptors, (PsiFunctor) element);
          } else if (element instanceof PsiTag) {
            foldTag(descriptors, (PsiTag) element);
          } else if (element instanceof OclYaccHeader) {
            foldHeader(descriptors, (OclYaccHeader) element);
          } else if (element instanceof OclYaccRule) {
            foldRule(descriptors, (OclYaccRule) element);
          } else if (types.MULTI_COMMENT == element.getNode().getElementType()) {
            FoldingDescriptor fold = fold(element);
            if (fold != null) {
              descriptors.add(fold);
            }
          }

          return true;
        });

    return descriptors.toArray(new FoldingDescriptor[0]);
  }

  private void foldLet(
      @NotNull List<FoldingDescriptor> descriptors, @NotNull PsiLet letExpression) {
    FoldingDescriptor fold = fold(letExpression.getBinding());
    if (fold != null) {
      descriptors.add(fold);
    }
  }

  private void foldType(
      @NotNull List<FoldingDescriptor> descriptors, @NotNull PsiType typeExpression) {
    PsiElement constrName =
        ORUtil.findImmediateFirstChildOfClass(typeExpression, PsiLowerIdentifier.class);
    if (constrName != null) {
      PsiElement binding = typeExpression.getBinding();
      if (binding != null && binding.getTextLength() > 5) {
        descriptors.add(new FoldingDescriptor(typeExpression, binding.getTextRange()));
      }
    }
  }

  private void foldModule(
      @NotNull List<FoldingDescriptor> descriptors, @NotNull PsiInnerModule module) {
    FoldingDescriptor foldSignature = fold(module.getModuleType());
    if (foldSignature != null) {
      descriptors.add(foldSignature);
    }

    FoldingDescriptor foldBody = fold(module.getBody());
    if (foldBody != null) {
      descriptors.add(foldBody);
    }
  }

  private void foldFunction(
      @NotNull List<FoldingDescriptor> descriptors, @NotNull PsiFunction func) {
    FoldingDescriptor foldBinding = fold(func.getBody());
    if (foldBinding != null) {
      descriptors.add(foldBinding);
    }
  }

  private void foldFunctor(
      @NotNull List<FoldingDescriptor> descriptors, @NotNull PsiFunctor functor) {
    FoldingDescriptor foldBinding = fold(functor.getBinding());
    if (foldBinding != null) {
      descriptors.add(foldBinding);
    }
  }

  private void foldTag(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiTag tag) {
    PsiTagStart start = ORUtil.findImmediateFirstChildOfClass(tag, PsiTagStart.class);
    PsiTagClose close =
        start == null ? null : ORUtil.findImmediateFirstChildOfClass(tag, PsiTagClose.class);
    // Auto-closed tags are not foldable
    if (close != null) {
      PsiElement lastChild = start.getLastChild();
      TextRange textRange =
          TextRange.create(lastChild.getTextOffset(), tag.getTextRange().getEndOffset() - 1);
      descriptors.add(new FoldingDescriptor(tag, textRange));
    }
  }

  private void foldHeader(
      @NotNull List<FoldingDescriptor> descriptors, @NotNull OclYaccHeader root) {
    FoldingDescriptor fold =
        fold(ORUtil.findImmediateFirstChildOfType(root, OclYaccLazyTypes.OCAML_LAZY_NODE));
    if (fold != null) {
      descriptors.add(fold);
    }
  }

  private void foldRule(@NotNull List<FoldingDescriptor> descriptors, @NotNull OclYaccRule root) {
    FoldingDescriptor fold =
        fold(ORUtil.findImmediateFirstChildOfClass(root, OclYaccRuleBody.class));
    if (fold != null) {
      descriptors.add(fold);
    }
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    IElementType elementType = node.getElementType();
    if (elementType == RmlTypes.INSTANCE.MULTI_COMMENT) {
      return "/*...*/";
    } else if (elementType == OclTypes.INSTANCE.MULTI_COMMENT) {
      return "(*...*)";
    } else if (elementType == OclTypes.INSTANCE.C_MODULE_TYPE) {
      return "sig...";
    }

    return "...";
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return false;
  }

  @Nullable
  private FoldingDescriptor fold(@Nullable PsiElement element) {
    if (element == null) {
      return null;
    }
    TextRange textRange = element.getTextRange();
    return textRange.getLength() > 5 ? new FoldingDescriptor(element, textRange) : null;
  }
}
