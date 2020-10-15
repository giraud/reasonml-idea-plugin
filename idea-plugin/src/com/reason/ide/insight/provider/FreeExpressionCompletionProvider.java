package com.reason.ide.insight.provider;

import static com.reason.lang.core.ExpressionFilterConstants.NO_FILTER;
import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static com.reason.lang.core.psi.ExpressionScope.pub;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PsiIconUtil;
import com.reason.Log;
import com.reason.ide.IconProvider;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.psi.impl.PsiFakeModule;
import com.reason.lang.core.signature.PsiSignatureUtil;
import icons.ORIcons;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public class FreeExpressionCompletionProvider {

  private static final Log LOG = Log.create("insight.free");

  public static void addCompletions(
      @NotNull QNameFinder qnameFinder,
      @NotNull PsiElement element,
      @NotNull CompletionResultSet resultSet) {
    LOG.debug("FREE expression completion");

    Project project = element.getProject();
    FileBase containingFile = (FileBase) element.getContainingFile();
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiFinder psiFinder = PsiFinder.getInstance(project);
    FileModuleIndexService fileIndex = FileModuleIndexService.getService();

    // Add virtual namespaces
    Collection<String> namespaces = fileIndex.getNamespaces(project);
    LOG.debug("  namespaces", namespaces);

    for (String namespace : namespaces) {
      resultSet.addElement(
          LookupElementBuilder.create(namespace)
              .withTypeText("Generated namespace")
              .withIcon(ORIcons.VIRTUAL_NAMESPACE));
    }

    // Add file modules (that are not a component and without namespaces)
    // everything scope is needed to retrieve files from node_modules
    Set<PsiFakeModule> topModules = psiFinder.findTopModules(true, scope);
    if (LOG.isDebugEnabled()) {
      LOG.debug("  files without namespaces", topModules);
    }

    for (PsiFakeModule topModule : topModules) {
      if (!topModule.getContainingFile().equals(containingFile)) {
        resultSet.addElement(
            LookupElementBuilder.create(topModule.getModuleName())
                .withTypeText(
                    FileHelper.shortLocation(
                        project, topModule.getContainingFile().getVirtualFile().getPath()))
                .withIcon(
                    IconProvider.getFileModuleIcon((FileBase) topModule.getContainingFile())));
      }
    }

    Set<String> paths = qnameFinder.extractPotentialPaths(element);
    paths.add("Pervasives");
    LOG.debug("potential paths", paths);

    // Add paths (opens and local opens for example)
    for (String path : paths) {
      Set<PsiModule> modulesFromQn =
          psiFinder.findModulesFromQn(path, true, interfaceOrImplementation, scope);
      for (PsiModule module : modulesFromQn) {
        if (module.getContainingFile().equals(containingFile)) {
          // if the module is already the containing file, we do nothing,
          // local expressions will be added after
          continue;
        }

        Collection<PsiNamedElement> expressions = module.getExpressions(pub, NO_FILTER);
        for (PsiNamedElement expression : expressions) {
          if (!(expression instanceof PsiAnnotation)) {
            resultSet.addElement(
                LookupElementBuilder.create(expression)
                    .withTypeText(PsiSignatureUtil.getSignature(expression, element.getLanguage()))
                    .withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                    .withInsertHandler(FreeExpressionCompletionProvider::insertExpression));
          }
        }
      }
    }

    // Add all local expressions
    PsiElement item = element.getPrevSibling();
    if (item == null) {
      item = element.getParent();
    }

    while (item != null) {
      if (item instanceof PsiInnerModule
          || item instanceof PsiLet
          || item instanceof PsiType
          || item instanceof PsiExternal
          || item instanceof PsiException
          || item instanceof PsiVal) {
        if (item instanceof PsiLet && ((PsiLet) item).isDeconsruction()) {
          for (PsiElement deconstructedElement : ((PsiLet) item).getDeconstructedElements()) {
            resultSet.addElement(
                LookupElementBuilder.create(deconstructedElement.getText())
                    .withTypeText(PsiSignatureUtil.getSignature(item, element.getLanguage()))
                    .withIcon(ORIcons.LET));
          }
        } else {
          PsiNamedElement expression = (PsiNamedElement) item;
          resultSet.addElement(
              LookupElementBuilder.create(expression)
                  .withTypeText(PsiSignatureUtil.getSignature(expression, element.getLanguage()))
                  .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
          if (item instanceof PsiType) {
            expandType((PsiType) item, resultSet);
          }
        }
      }

      PsiElement prevItem = item.getPrevSibling();
      if (prevItem == null) {
        PsiElement parent = item.getParent();
        item = parent instanceof PsiInnerModule ? parent.getPrevSibling() : parent;
      } else {
        item = prevItem;
      }
    }
  }

  private static void expandType(@NotNull PsiType type, @NotNull CompletionResultSet resultSet) {
    Collection<PsiVariantDeclaration> variants = type.getVariants();
    if (!variants.isEmpty()) {
      for (PsiVariantDeclaration variant : variants) {
        resultSet.addElement(
            LookupElementBuilder.create(variant)
                .withTypeText(type.getName())
                .withIcon(PsiIconUtil.getProvidersIcon(variant, 0)));
      }
    }
  }

  private static void insertExpression(
      @NotNull InsertionContext insertionContext, @NotNull LookupElement element) {
    PsiElement psiElement = element.getPsiElement();
    if (psiElement instanceof PsiLet) {
      PsiLet let = (PsiLet) psiElement;
      if (let.isFunction()) {
        insertionContext.setAddCompletionChar(false);
        Editor editor = insertionContext.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, "()");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
      }
    }
  }
}
