package com.reason.ide.insight.provider;

import static com.reason.lang.core.ExpressionFilterConstants.NO_FILTER;
import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static com.reason.lang.core.psi.ExpressionScope.pub;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PsiIconUtil;
import com.intellij.util.containers.ArrayListSet;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.ide.IconProvider;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.IndexedFileModule;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.psi.impl.PsiFunctorCall;
import com.reason.lang.core.signature.PsiSignatureUtil;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public class DotExpressionCompletionProvider {

  private static final Log LOG = Log.create("insight.dot");

  private DotExpressionCompletionProvider() {}

  public static void addCompletions(
      @NotNull QNameFinder qnameFinder,
      @NotNull PsiElement element,
      @NotNull CompletionResultSet resultSet) {
    LOG.debug("DOT expression completion");

    Project project = element.getProject();
    PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
    PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

    if (previousElement instanceof PsiUpperSymbol) {
      String upperName = previousElement.getText();
      if (upperName != null) {
        LOG.debug(" -> symbol", upperName);
        PsiFinder psiFinder = PsiFinder.getInstance(project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        // Find potential module paths, and filter the result
        Set<String> potentialPaths = qnameFinder.extractPotentialPaths(element);
        if (LOG.isTraceEnabled()) {
          LOG.debug(" -> paths", potentialPaths);
        }

        Set<PsiModule> resolvedModules = new ArrayListSet<>();
        for (String qname : potentialPaths) {
          Set<PsiModule> modulesFromQn =
              psiFinder.findModulesFromQn(qname, true, interfaceOrImplementation, scope);
          for (PsiModule module : modulesFromQn) {
            PsiFunctorCall functorCall = module.getFunctorCall();
            if (functorCall != null) {
              // resolve functor definition
              String functorName = functorCall.getFunctorName();
              potentialPaths = qnameFinder.extractPotentialPaths(module);
              for (String qnameFunctorPath : potentialPaths) {
                Set<PsiModule> functorsFromQn =
                    psiFinder.findModulesFromQn(
                        qnameFunctorPath + "." + functorName,
                        true,
                        interfaceOrImplementation,
                        scope);
                for (PsiModule functorModule : functorsFromQn) {
                  PsiFunctor functor = (PsiFunctor) functorModule;
                  PsiElement returnType = functor.getReturnType();
                  if (returnType == null) {
                    resolvedModules.add(functor);
                  } else {
                    // resolve return type
                    Set<PsiModule> interfacesFromQn =
                        psiFinder.findModulesFromQn(
                            qnameFunctorPath + "." + returnType.getText(),
                            true,
                            interfaceOrImplementation,
                            scope);
                    resolvedModules.addAll(interfacesFromQn);
                  }
                }
              }
            } else {
              resolvedModules.add(module);
            }
          }
        }
        LOG.debug(" -> resolved modules from path", resolvedModules);

        // Might be a virtual namespace

        Collection<IndexedFileModule> modulesForNamespace =
            psiFinder.findModulesForNamespace(upperName, scope);
        if (!modulesForNamespace.isEmpty()) {
          LOG.debug("  found namespace files", modulesForNamespace);

          for (IndexedFileModule file : modulesForNamespace) {
            resultSet.addElement(
                LookupElementBuilder.create(file.getModuleName())
                    .withTypeText(FileHelper.shortLocation(project, file.getPath()))
                    .withIcon(IconProvider.getFileModuleIcon(file.isOCaml(), file.isInterface())));
          }

          return;
        }

        // Use first resolved module

        if (!resolvedModules.isEmpty()) {
          Collection<PsiNamedElement> expressions =
              resolvedModules.iterator().next().getExpressions(pub, NO_FILTER);
          LOG.trace(" -> expressions", expressions);
          addExpressions(resultSet, expressions, element.getLanguage());
        }
      }
    } else if (previousElement instanceof PsiLowerSymbol) {
      // Expression of let/val/external/type
      String lowerName = previousElement.getText();
      if (lowerName != null) {
        LOG.debug("  symbol", lowerName);
        PsiFinder psiFinder = PsiFinder.getInstance(project);

        // try let
        Collection<PsiLet> lets = psiFinder.findLets(lowerName, interfaceOrImplementation);
        if (LOG.isDebugEnabled()) {
          LOG.debug(
              "  lets",
              lets.size(),
              lets.size() == 1
                  ? " (" + lets.iterator().next().getName() + ")"
                  : "[" + Joiner.join(", ", lets) + "]");
        }

        // need filtering

        for (PsiLet expression : lets) {
          for (PsiRecordField recordField : expression.getRecordFields()) {
            resultSet.addElement(
                LookupElementBuilder.create(recordField)
                    .withTypeText(PsiSignatureUtil.getSignature(recordField, element.getLanguage()))
                    .withIcon(PsiIconUtil.getProvidersIcon(recordField, 0)));
          }
        }
      }
    }
  }

  private static void addExpressions(
      @NotNull CompletionResultSet resultSet,
      @NotNull Collection<PsiNamedElement> expressions,
      @NotNull Language language) {
    for (PsiNamedElement expression : expressions) {
      if (!(expression instanceof PsiOpen)
          && !(expression instanceof PsiInclude)
          && !(expression instanceof PsiAnnotation)) {
        // TODO: if include => include
        String name = expression.getName();
        if (name != null) {
          String signature = PsiSignatureUtil.getSignature(expression, language);
          resultSet.addElement(
              LookupElementBuilder.create(name)
                  .withTypeText(signature)
                  .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
        }
        if (expression instanceof PsiType) {
          PsiType eType = (PsiType) expression;
          Collection<PsiVariantDeclaration> variants = eType.getVariants();
          if (!variants.isEmpty()) {
            for (PsiVariantDeclaration variant : variants) {
              String variantName = variant.getName();
              if (variantName != null) {
                resultSet.addElement(
                    LookupElementBuilder.create(variantName)
                        .withTypeText(eType.getName())
                        .withIcon(PsiIconUtil.getProvidersIcon(variant, 0)));
              }
            }
          }
        }
      }
    }
  }
}
