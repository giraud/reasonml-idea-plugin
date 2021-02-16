package com.reason.lang.core.psi.impl;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static java.util.Collections.emptyList;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModuleHelper;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.ExpressionFilter;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiInnerModuleImpl extends PsiTokenStub<ORTypes, PsiModule, PsiModuleStub> implements PsiInnerModule {
  // region Constructors
  public PsiInnerModuleImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  public PsiInnerModuleImpl(
      @NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
    super(types, stub, nodeType);
  }
  // endregion

  // region NamedElement
  @Nullable
  @Override
  public String getName() {
    PsiElement nameIdentifier = findChildByClass(PsiUpperIdentifier.class);
    return nameIdentifier == null ? null : nameIdentifier.getText();
  }

  @NotNull
  @Override
  public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
    return this;
  }
  // endregion

  @NotNull
  @Override
  public String getModuleName() {
    String name = getName();
    return name == null ? "" : name;
  }

  @Override
  public boolean isInterface() {
    return ((FileBase) getContainingFile()).isInterface();
  }

  @Nullable
  @Override
  public PsiFunctorCall getFunctorCall() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorCall.class);
  }

  @NotNull
  @Override
  public String getPath() {
    PsiModuleStub stub = getGreenStub();
    if (stub != null) {
      return stub.getPath();
    }

    return ORUtil.getQualifiedPath(this);
  }

  @NotNull
  @Override
  public String getQualifiedName() {
    PsiModuleStub stub = getGreenStub();
    if (stub != null) {
      return stub.getQualifiedName();
    }

    return ORUtil.getQualifiedName(this);
  }

  @Nullable
  public PsiElement getBody() {
    return ORUtil.findImmediateFirstChildOfAnyClass(this, PsiScopedExpr.class, PsiStruct.class);
  }

  @Nullable
  public PsiModuleType getModuleType() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiModuleType.class);
  }

  @NotNull
  @Override
  public Collection<PsiModule> getModules() {
    PsiElement body = getBody();
    return body == null
        ? emptyList()
        : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiInnerModule.class);
  }

  @Nullable
  @Override
  public PsiModule getModuleExpression(@Nullable String name) {
    if (name != null) {
      for (PsiModule module : getModules()) {
        if (name.equals(module.getName())) {
          return module;
        }
      }
    }

    return null;
  }

  @NotNull
  @Override
  public Collection<PsiNamedElement> getExpressions(
      @NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter) {
    Collection<PsiNamedElement> result = emptyList();

    PsiFinder psiFinder = PsiFinder.getInstance(getProject());

    String alias = getAlias();
    if (alias != null) {
      // Open alias and getExpressions on alias
      Set<PsiModule> modulesByName =
          psiFinder.findModulesbyName(
              alias, interfaceOrImplementation, null, GlobalSearchScope.allScope(getProject()));
      if (!modulesByName.isEmpty()) {
        PsiModule moduleAlias = modulesByName.iterator().next();
        if (moduleAlias != null) {
          result = moduleAlias.getExpressions(eScope, filter);
        }
      }
    } else {
      PsiModuleType moduleType = getModuleType();
      if (moduleType == null) {
        PsiElement body = getBody();
        if (body == null) {
          PsiFunctorCall functorCall =
              ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorCall.class);
          if (functorCall != null) {
            result = new ArrayList<>();
            // Include all expressions from functor
            QNameFinder qnameFinder = PsiFinder.getQNameFinder(getLanguage());

            Set<String> potentialPaths = qnameFinder.extractPotentialPaths(functorCall);
            for (String potentialPath : potentialPaths) {
              Set<PsiModule> modules =
                  psiFinder.findModulesFromQn(
                      potentialPath + "." + functorCall.getFunctorName(),
                      true,
                      interfaceOrImplementation,
                      GlobalSearchScope.allScope(getProject()));
              for (PsiModule module : modules) {
                result.addAll(module.getExpressions(eScope, filter));
              }
            }

            Set<PsiModule> modules =
                psiFinder.findModulesFromQn(
                    functorCall.getFunctorName(),
                    true,
                    interfaceOrImplementation,
                    GlobalSearchScope.allScope(getProject()));
            for (PsiModule module : modules) {
              result.addAll(module.getExpressions(eScope, filter));
            }
          }
        } else {
          result = new ArrayList<>();
          PsiElement element = body.getFirstChild();
          while (element != null) {
            if (element instanceof PsiNamedElement
                && (filter == null || filter.accept((PsiNamedElement) element))) {
              result.add((PsiNamedElement) element);
            }
            element = element.getNextSibling();
          }
        }
      } else {
        result = new ArrayList<>();
        PsiElement element = moduleType.getFirstChild();
        while (element != null) {
          if (element instanceof PsiNamedElement
              && (filter == null || filter.accept((PsiNamedElement) element))) {
            result.add((PsiNamedElement) element);
          }
          element = element.getNextSibling();
        }
      }
    }

    return result;
  }

  @Nullable
  @Override
  public PsiType getTypeExpression(@Nullable String name) {
    PsiElement body = name == null ? null : getBody();
    if (body != null) {
      ExpressionFilter expressionFilter =
          element -> element instanceof PsiType && name.equals(element.getName());
      Collection<PsiNamedElement> expressions =
          getExpressions(ExpressionScope.all, expressionFilter);
      if (!expressions.isEmpty()) {
        return (PsiType) expressions.iterator().next();
      }
    }

    return null;
  }

  @Nullable
  @Override
  public PsiLet getLetExpression(@Nullable String name) {
    PsiElement body = name == null ? null : getBody();
    if (body != null) {
      ExpressionFilter expressionFilter =
          element -> element instanceof PsiLet && name.equals(element.getName());
      Collection<PsiNamedElement> expressions =
          getExpressions(ExpressionScope.all, expressionFilter);
      if (!expressions.isEmpty()) {
        return (PsiLet) expressions.iterator().next();
      }
    }

    return null;
  }

  @Nullable
  @Override
  public PsiVal getValExpression(@Nullable String name) {
    PsiElement body = name == null ? null : getBody();
    if (body != null) {
      ExpressionFilter expressionFilter =
          element -> element instanceof PsiVal && name.equals(element.getName());
      Collection<PsiNamedElement> expressions =
          getExpressions(ExpressionScope.all, expressionFilter);
      if (!expressions.isEmpty()) {
        return (PsiVal) expressions.iterator().next();
      }
    }

    return null;
  }

  private boolean isModuleTypeOf() {
    PsiElement nextSibling = ORUtil.nextSibling(getFirstChild());
    PsiElement nextNextSibling = ORUtil.nextSibling(nextSibling);
    return nextSibling != null
        && nextNextSibling != null
        && nextSibling.getNode().getElementType() == m_types.TYPE
        && nextNextSibling.getNode().getElementType() == m_types.OF;
  }

  private @Nullable PsiModule findReferencedModuleTypeOf() {
    PsiElement of = ORUtil.findImmediateFirstChildOfType(this, m_types.OF);

    if (of != null) {
      // find latest module name
      PsiElement module = ORUtil.nextSiblingWithTokenType(of, m_types.C_UPPER_SYMBOL);
      PsiElement moduleNextSibling = module == null ? null : module.getNextSibling();
      while (moduleNextSibling != null
          && moduleNextSibling.getNode().getElementType() == m_types.DOT) {
        PsiElement element = moduleNextSibling.getNextSibling();
        if (element != null && element.getNode().getElementType() == m_types.C_UPPER_SYMBOL) {
          module = element;
          moduleNextSibling = module.getNextSibling();
        } else {
          moduleNextSibling = null;
        }
      }

      if (module != null) {
        PsiReference reference = module.getReference();
        PsiElement resolvedElement = reference == null ? null : reference.resolve();
        if (resolvedElement instanceof PsiUpperIdentifier) {
          PsiElement resolvedModule = resolvedElement.getParent();
          if (resolvedModule instanceof PsiModule) {
            return (PsiModule) resolvedModule;
          }
        }
      }
    }

    return null;
  }

  public ItemPresentation getPresentation() {
    boolean isModuleTypeOf = isModuleTypeOf();
    PsiModule referencedModuleType = isModuleTypeOf ? findReferencedModuleTypeOf() : null;

    return new ItemPresentation() {
      @Nullable
      @Override
      public String getPresentableText() {
        if (isModuleTypeOf) {
          if (referencedModuleType == null) {
            PsiElement of =
                ORUtil.findImmediateFirstChildOfType(PsiInnerModuleImpl.this, m_types.OF);
            assert of != null;
            return getText().substring(of.getStartOffsetInParent() + 3);
          }
          return referencedModuleType.getName();
        }
        return getName();
      }

      @NotNull
      @Override
      public String getLocationString() {
        return referencedModuleType == null
            ? ""
            : referencedModuleType.getContainingFile().getName();
      }

      @NotNull
      @Override
      public Icon getIcon(boolean unused) {
        return isModuleType()
            ? ORIcons.MODULE_TYPE
            : (isInterface() ? ORIcons.INNER_MODULE_INTF : ORIcons.INNER_MODULE);
      }
    };
  }

  @Override
  public boolean isComponent() {
    PsiModuleStub stub = getGreenStub();
    if (stub != null) {
      return stub.isComponent();
    }

    return ModuleHelper.isComponent(getBody());
  }

  @Override
  public boolean isModuleType() {
    PsiElement psiElement = ORUtil.nextSibling(getFirstChild());
    return psiElement != null && psiElement.getNode().getElementType() == m_types.TYPE;
  }

  @NotNull
  @Override
  public PsiElement getNavigationElement() {
    if (isComponent()) {
      PsiLet make = getLetExpression("make");
      if (make != null) {
        return make;
      }
    }
    return super.getNavigationElement();
  }

  @Override
  @Nullable
  public String getAlias() {
    PsiModuleStub stub = getGreenStub();
    if (stub != null) {
      return stub.getAlias();
    }

    PsiElement eq = findChildByType(m_types.EQ);
    if (eq != null) {
      return ORUtil.computeAlias(eq.getNextSibling(), getLanguage(), false);
    }

    return null;
  }

  @Nullable
  @Override
  public String toString() {
    return "Module " + getQualifiedName();
  }
}
