package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.intellij.util.containers.*;
import com.reason.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.signature.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import static com.reason.lang.core.ExpressionFilterConstants.*;
import static com.reason.lang.core.ORFileType.*;
import static com.reason.lang.core.psi.ExpressionScope.*;

public class DotExpressionCompletionProvider {

  private static final Log LOG = Log.create("insight.dot");

  private DotExpressionCompletionProvider() {
  }

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

        Collection<IndexedFileModule> modulesForNamespace = psiFinder.findModulesForNamespace(upperName, scope);
        if (!modulesForNamespace.isEmpty()) {
          LOG.debug("  found namespace files", modulesForNamespace);

          VirtualFileManager vFileManager = VirtualFileManager.getInstance();
          PsiManager psiManager = PsiManager.getInstance(project);

          for (IndexedFileModule file : modulesForNamespace) {
            VirtualFile fileByNioPath = null;
            try {
              fileByNioPath = vFileManager.findFileByUrl(new File(file.getPath()).toURI().toURL().toString());
            } catch (MalformedURLException e) {
              e.printStackTrace();
            }
            PsiFile psiFile = fileByNioPath == null ? null : psiManager.findFile(fileByNioPath);
            resultSet.addElement(
                LookupElementBuilder.create(file.getModuleName())
                    .withTypeText(psiFile == null ? file.getPath() : FileHelper.shortLocation(psiFile))
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
