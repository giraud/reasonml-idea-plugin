package com.reason.ide.go;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.daemon.impl.GutterIconTooltipHelper;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Icons;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVal;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiElement parent = element.getParent();
        GlobalSearchScope scope = GlobalSearchScope.allScope(element.getProject());
        PsiManager instance = PsiManager.getInstance(element.getProject());
        FileBase containingFile = (FileBase) element.getContainingFile();

        if (element instanceof PsiTypeConstrName) {
            Collection<VirtualFile> files = findRelatedFiles(containingFile, scope);
            if (files.size() == 1) {
                VirtualFile relatedFile = files.iterator().next();
                FileBase psiFile = (FileBase) instance.findFile(relatedFile);
                if (psiFile != null) {
                    Collection<PsiType> expressions = psiFile.getExpressions(element.getText(), PsiType.class);
                    if (expressions.size() == 1) {
                        PsiType relatedType = expressions.iterator().next();
                        PsiElement symbol = PsiTreeUtil.findChildOfType(element, PsiLowerSymbol.class);
                        PsiElement relatedSymbol = PsiTreeUtil.findChildOfType(relatedType, PsiLowerSymbol.class);
                        if (symbol != null && relatedSymbol != null) {
                            result.add(NavigationGutterIconBuilder.
                                    create(containingFile.isInterface() ? Icons.IMPLEMENTED : Icons.IMPLEMENTING).
                                    setAlignment(GutterIconRenderer.Alignment.RIGHT).
                                    setTargets(Collections.singleton(relatedSymbol.getFirstChild())).
                                    createLineMarkerInfo(symbol.getFirstChild()));
                        }
                    }
                }
            }
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiLet && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, scope, instance, containingFile);
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiVal && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, scope, instance, containingFile);
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiExternal && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, scope, instance, containingFile);
        } else if (element instanceof PsiUpperSymbol && parent instanceof PsiInnerModule && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, scope, instance, containingFile);
        }
    }

    private void extractRelatedExpressions(@Nullable PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result,
                                           @NotNull GlobalSearchScope scope, @NotNull PsiManager instance, @NotNull FileBase containingFile) {
        if (element == null) {
            return;
        }

        Collection<VirtualFile> files = findRelatedFiles(containingFile, scope);
        if (files.size() == 1) {
            VirtualFile relatedFile = files.iterator().next();
            FileBase psiRelatedFile = (FileBase) instance.findFile(relatedFile);
            if (psiRelatedFile != null) {
                Collection<PsiNameIdentifierOwner> expressions = psiRelatedFile.getExpressions(element.getText());
                if (expressions.size() == 1) {
                    PsiNameIdentifierOwner relatedElement = expressions.iterator().next();
                    PsiElement nameIdentifier = relatedElement.getNameIdentifier();
                    String tooltip = GutterIconTooltipHelper
                            .composeText(new PsiElement[]{psiRelatedFile}, "", "Implements method <b>" + nameIdentifier.getText() + "</b> in <b>{0}</b>");
                    result.add(NavigationGutterIconBuilder.
                            create(containingFile.isInterface() ? Icons.IMPLEMENTED : Icons.IMPLEMENTING).
                            setTooltipText(tooltip).
                            setAlignment(GutterIconRenderer.Alignment.RIGHT).
                            setTargets(Collections.singleton(nameIdentifier instanceof PsiLowerSymbol ? nameIdentifier.getFirstChild() : nameIdentifier)).
                            createLineMarkerInfo(element));
                }
            }
        }
    }

    @NotNull
    private Collection<VirtualFile> findRelatedFiles(@NotNull FileBase file, @NotNull GlobalSearchScope scope) {
        FileModuleIndexService fileModuleIndex = FileModuleIndexService.getService();
        if (file.isInterface()) {
            return fileModuleIndex.getImplementationFilesWithName(file.asModuleName(), scope);
        }
        return fileModuleIndex.getInterfaceFilesWithName(file.asModuleName(), scope);
    }
}
