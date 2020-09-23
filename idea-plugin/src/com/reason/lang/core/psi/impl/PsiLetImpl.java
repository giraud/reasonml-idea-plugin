package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.stub.PsiLetStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.reason.RmlTypes;
import icons.ORIcons;
import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiLetImpl extends PsiTokenStub<ORTypes, PsiLetStub> implements PsiLet {

  private ORSignature m_inferredType = ORSignature.EMPTY;

  // region Constructors
  public PsiLetImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  public PsiLetImpl(
      @NotNull ORTypes types, @NotNull PsiLetStub stub, @NotNull IStubElementType nodeType) {
    super(types, stub, nodeType);
  }
  // endregion

  // region PsiNamedElement
  @Nullable
  public PsiElement getNameIdentifier() {
    return ORUtil.findImmediateFirstChildOfAnyClass(
        this, PsiLowerIdentifier.class, PsiScopedExpr.class, PsiDeconstruction.class);
  }

  @Nullable
  @Override
  public String getName() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null || nameIdentifier.getNode().getElementType() == m_types.UNDERSCORE
        ? null
        : nameIdentifier.getText();
  }

  @NotNull
  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return this;
  }
  // endregion

  @Override
  @Nullable
  public PsiLetBinding getBinding() {
    return findChildByClass(PsiLetBinding.class);
  }

  @Override
  public boolean isScopeIdentifier() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiDeconstruction.class) != null;
  }

  @NotNull
  @Override
  public Collection<PsiElement> getScopeChildren() {
    Collection<PsiElement> result = new ArrayList<>();

    PsiElement scope = ORUtil.findImmediateFirstChildOfClass(this, PsiDeconstruction.class);
    if (scope != null) {
      for (PsiElement element : scope.getChildren()) {
        if (element.getNode().getElementType() != m_types.COMMA) {
          result.add(element);
        }
      }
    }

    return result;
  }

  @NotNull
  private static List<PsiObjectField> getJsObjectFields(
      @NotNull PsiElement parent,
      @NotNull Map<PsiElement, Boolean> visited,
      @NotNull List<String> path,
      int offset) {
    List<PsiObjectField> fields = new ArrayList<>();
    PsiElement prevParent = null;
    boolean isAdding = false;

    // depth first elements
    Collection<PsiElement> elements =
        PsiTreeUtil.findChildrenOfAnyType(parent, PsiObjectField.class, PsiLowerSymbol.class);
    for (PsiElement element : elements) {
      if (visited.containsKey(element)) {
        continue;
      }
      visited.put(element, true);

      // { "fieldName": currentLet } - element is "fieldName"
      if (element instanceof PsiObjectField && element.getParent() instanceof PsiJsObject) {
        String name = ((PsiObjectField) element).getName();
        if (prevParent == null) {
          prevParent = element.getParent().getParent();
        }

        if (offset >= path.size()) {
          if (Objects.equals(element.getParent().getParent(), prevParent)) {
            isAdding = true;
            fields.add((PsiObjectField) element);
          } else if (isAdding) {
            isAdding = false;
            offset = offset > 0 ? offset - 1 : 0;
          } else {
            int prev = offset > 0 ? offset - 1 : 0;
            String lookingFor = path.get(prev);
            if (name != null && name.equals(lookingFor)) {
              prevParent = element;
            }
          }
        } else {
          String lookingFor = path.get(offset);
          if (name != null && name.equals(lookingFor)) {
            prevParent = element;
            offset = offset + 1;
          }
        }
      }
      // { "fieldName": currentLet } - element is currentLet
      else if (element instanceof PsiLowerSymbol) {
        if (path.isEmpty()) {
          continue;
        }

        String name = element.getText();

        if (name != null) {
          Collection<PsiLet> lets =
              PsiFinder.getInstance(element.getProject())
                  .findLets(name, ORFileType.interfaceOrImplementation);
          if (!lets.isEmpty()) {
            PsiLet let = lets.iterator().next();
            if (let == null) {
              continue;
            }
            if (let == parent) {
              continue;
            }

            int prev = offset > 0 ? offset - 1 : 0;
            String lookingFor = path.get(prev);

            // fieldName that is referencing current let { "fieldName": currentLet };
            PsiElement elementParent = element.getParent();
            if (elementParent instanceof PsiObjectField) {
              PsiElement elementGrandParent = elementParent.getParent();
              if (elementGrandParent instanceof PsiJsObject) {
                String fieldName = ((PsiObjectField) element.getParent()).getName();
                if (fieldName != null && fieldName.equals(lookingFor)) {
                  fields.addAll(getJsObjectFields(let, visited, path, offset));
                }
              }
            }
          }
        }
      }
    }
    return fields;
  }

  @Nullable
  @Override
  public String getAlias() {
    PsiLetStub stub = getGreenStub();
    if (stub != null) {
      return stub.getAlias();
    }

    PsiElement binding = getBinding();
    if (binding != null) {
      return ORUtil.computeAlias(binding.getFirstChild(), getLanguage(), true);
    }

    return null;
  }

  @Override
  @Nullable
  public PsiSignature getPsiSignature() {
    return findChildByClass(PsiSignature.class);
  }

  @NotNull
  @Override
  public ORSignature getORSignature() {
    PsiSignature signature = getPsiSignature();
    return signature == null ? ORSignature.EMPTY : signature.asHMSignature();
  }

  @Override
  public boolean isFunction() {
    PsiLetStub stub = getGreenStub();
    if (stub != null) {
      return stub.isFunction();
    }

    if (hasInferredType()) {
      return getInferredType().isFunctionSignature();
    } else {
      ORSignature signature = getORSignature();
      if (signature != ORSignature.EMPTY) {
        return signature.isFunctionSignature();
      }
    }

    if (m_types instanceof RmlTypes) {
      PsiLetBinding binding = findChildByClass(PsiLetBinding.class);
      return binding != null && binding.getFirstChild() instanceof PsiFunction;
    }

    return PsiTreeUtil.findChildOfType(this, PsiFunction.class) != null;
  }

  @Nullable
  public PsiFunction getFunction() {
    PsiLetBinding binding = getBinding();
    if (binding != null) {
      PsiElement child = binding.getFirstChild();
      if (child instanceof PsiFunction) {
        return (PsiFunction) child;
      }
    }
    return null;
  }

  @Override
  public boolean isRecord() {
    return findChildByClass(PsiRecord.class) != null;
  }

  @Override
  public boolean isJsObject() {
    return PsiTreeUtil.findChildOfType(this, PsiJsObject.class) != null;
  }

  @NotNull
  @Override
  public Collection<PsiRecordField> getRecordFields() {
    return PsiTreeUtil.findChildrenOfType(this, PsiRecordField.class);
  }

  @NotNull
  @Override
  public Collection<PsiObjectField> getJsObjectFieldsForPath(@NotNull List<String> path) {
    WeakHashMap<PsiElement, Boolean> visited = new WeakHashMap<>();
    return getJsObjectFields(this, visited, new ArrayList<>(path), 0);
  }

  private boolean isRecursive() {
    // Find first element after the LET
    PsiElement firstChild = getFirstChild();
    PsiElement sibling = firstChild.getNextSibling();
    if (sibling instanceof PsiWhiteSpace) {
      sibling = sibling.getNextSibling();
    }

    return sibling != null && "rec".equals(sibling.getText());
  }

  // region Inferred type
  @Override
  public ORSignature getInferredType() {
    return m_inferredType;
  }

  @Override
  public void setInferredType(ORSignature inferredType) {
    m_inferredType = inferredType;
  }

  @Override
  public boolean hasInferredType() {
    return m_inferredType != ORSignature.EMPTY;
  }
  // endregion

  @Override
  public @NotNull String getQualifiedName() {
    PsiLetStub stub = getGreenStub();
    if (stub != null) {
      return stub.getQualifiedName();
    }

    return ORUtil.getQualifiedName(this);
  }

  @NotNull
  @Override
  public String getPath() {
    return ORUtil.getQualifiedPath(this); // stub + name using this
  }

  @Override
  public boolean isDeconsruction() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier instanceof PsiDeconstruction;
  }

  @Override
  public boolean isPrivate() {
    PsiLetAttribute attribute = ORUtil.findImmediateFirstChildOfClass(this, PsiLetAttribute.class);
    String value = attribute == null ? null : attribute.getValue();
    return value != null && value.equals("private");
  }

  @NotNull
  @Override
  public List<PsiElement> getDeconstructedElements() {
    PsiElement nameIdentifier = getNameIdentifier();
    if (nameIdentifier instanceof PsiDeconstruction) {
      return ((PsiDeconstruction) nameIdentifier).getDeconstructedElements();
    }
    return Collections.emptyList();
  }

  // region PsiStructuredElement
  @Override
  public boolean canBeDisplayed() {
    PsiElement nameIdentifier = getNameIdentifier();
    if (nameIdentifier != null) {
      return true;
    }

    PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
    return scope != null && !scope.isEmpty();
  }

  @Nullable
  @Override
  public ItemPresentation getPresentation() {
    final PsiLet let = this;

    return new ItemPresentation() {
      @NotNull
      @Override
      public String getPresentableText() {
        PsiElement letValueName = getNameIdentifier();
        if (letValueName == null) {
          PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(let, PsiScopedExpr.class);
          return scope == null || scope.isEmpty() ? "_" : scope.getText();
        }

        String letName = letValueName.getText();
        if (isFunction()) {
          return letName + (isRecursive() ? " (rec)" : "");
        }

        return letName;
      }

      @Nullable
      @Override
      public String getLocationString() {
        ORSignature signature = hasInferredType() ? getInferredType() : getORSignature();
        return (signature == ORSignature.EMPTY ? null : signature.asString(getLanguage()));
      }

      @NotNull
      @Override
      public Icon getIcon(boolean unused) {
        return isFunction() ? ORIcons.FUNCTION : ORIcons.LET;
      }
    };
  }
  // endregion

  @Nullable
  @Override
  public String toString() {
    return "Let " + getQualifiedName();
  }
}
