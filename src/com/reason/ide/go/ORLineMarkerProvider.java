package com.reason.ide.go;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.Icons;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

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
                Collection<PsiType> expressions = psiFile.getExpressions(element.getText(), PsiType.class);
                if (expressions.size() == 1) {
                    PsiType relatedType = expressions.iterator().next();
                    result.add(NavigationGutterIconBuilder.
                            create(containingFile.isInterface() ? Icons.IMPLEMENTED : Icons.IMPLEMENTING).
                            setAlignment(GutterIconRenderer.Alignment.RIGHT).
                            setTargets(Collections.singleton(relatedType.getConstrName())).
                            createLineMarkerInfo(element));
                }
            }
        } else if (element instanceof PsiLetName) {
            extractRelatedExpressions(element, result, scope, instance, containingFile);
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiVal && ((PsiNamedElement) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element, result, scope, instance, containingFile);
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiExternal && ((PsiNamedElement) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element, result, scope, instance, containingFile);
        }
    }

    private void extractRelatedExpressions(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result, GlobalSearchScope scope, PsiManager instance, FileBase containingFile) {
        Collection<VirtualFile> files = findRelatedFiles(containingFile, scope);
        if (files.size() == 1) {
            VirtualFile relatedFile = files.iterator().next();
            FileBase psiFile = (FileBase) instance.findFile(relatedFile);
            if (psiFile != null) {
                Collection<PsiNamedElement> expressions = psiFile.getExpressions(element.getText());
                if (expressions.size() == 1) {
                    PsiNamedElement relatedLet = expressions.iterator().next();
                    result.add(NavigationGutterIconBuilder.
                            create(containingFile.isInterface() ? Icons.IMPLEMENTED : Icons.IMPLEMENTING).
                            setAlignment(GutterIconRenderer.Alignment.RIGHT).
                            setTargets(Collections.singleton(relatedLet.getNameIdentifier())).
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
