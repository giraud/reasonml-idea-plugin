package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.navigation.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
    private static final @NotNull Predicate<PsiElement> PSI_IMPL_PREDICATE = psiElement -> {
        FileBase psiFile = (FileBase) psiElement.getContainingFile();
        return !psiFile.isInterface() /*&& Platform.isSourceFile(psiFile) --> Not working with webstorm for ex */;
    };
    private static final @NotNull Predicate<PsiElement> PSI_INTF_PREDICATE = psiElement -> {
        FileBase psiFile = (FileBase) psiElement.getContainingFile();
        return psiFile.isInterface() /*&& Platform.isSourceFile(psiFile) --> Not working with webstorm for ex */;
    };

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        PsiElement parent = element.getParent();
        if (parent instanceof RPsiDeconstruction) {
            parent = parent.getParent();
        }

        Project project = element.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        FileBase containingFile = (FileBase) element.getContainingFile();
        boolean isInterface = containingFile.isInterface();

        if (element instanceof RPsiLowerSymbol) {
            if (parent instanceof RPsiLet) {
                String qNameLet = ((RPsiLetImpl) parent).getQualifiedName();
                if (((RPsiLet) parent).isDeconstruction()) {
                    qNameLet = Joiner.join(".", ORUtil.getQualifiedPath(parent)) + "." + element.getText();
                }
                final String qName = qNameLet;

                Collection<RPsiVal> vals = ValFqnIndex.getElements(qName.hashCode(), project, scope);
                vals.stream()
                        .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                        .findFirst()
                        .ifPresentOrElse(psiVal ->
                                        result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiVal.getContainingFile(), psiVal))
                                , () -> {
                                    Collection<RPsiLet> lets = LetFqnIndex.getElements(qName.hashCode(), project, scope);
                                    lets.stream()
                                            .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                                            .findFirst()
                                            .ifPresent(psiLet ->
                                                    result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiLet.getContainingFile(), psiLet))
                                            );
                                });
            } else if (parent instanceof RPsiExternal) {
                String externalQName = ((RPsiExternalImpl) parent).getQualifiedName();
                Collection<RPsiExternal> elements = ExternalFqnIndex.getElements(externalQName.hashCode(), project, scope);
                elements.stream()
                        .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                        .findFirst()
                        .ifPresent(psiTarget ->
                                result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiTarget.getContainingFile(), psiTarget))
                        );
            } else if (parent instanceof RPsiValImpl) {
                String valQName = ((RPsiValImpl) parent).getQualifiedName();
                Collection<RPsiLet> elements = LetFqnIndex.getElements(valQName.hashCode(), project, scope);
                elements.stream()
                        .filter(PSI_IMPL_PREDICATE)
                        .findFirst()
                        .ifPresent(psiTarget ->
                                result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiTarget.getContainingFile(), psiTarget))
                        );
            } else if (parent instanceof RPsiType) {
                String valQName = ((RPsiTypeImpl) parent).getQualifiedName();
                Collection<RPsiType> elements = TypeFqnIndex.getElements(valQName.hashCode(), project, scope);
                elements.stream()
                        .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                        .findFirst()
                        .ifPresent(psiTarget ->
                                result.add(createGutterIcon(element, isInterface, "type", (FileBase) psiTarget.getContainingFile(), psiTarget))
                        );
            } else if (parent instanceof RPsiClass) {
                String qName = ((RPsiClassImpl) parent).getQualifiedName();
                Collection<RPsiClass> elements = ClassFqnIndex.getElements(qName.hashCode(), project, scope);
                elements.stream()
                        .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                        .findFirst()
                        .ifPresent(psiTarget ->
                                result.add(createGutterIcon(element, isInterface, "class", (FileBase) psiTarget.getContainingFile(), psiTarget))
                        );
            } else if (parent instanceof RPsiClassMethodImpl) {
                String qName = ((RPsiClassMethodImpl) parent).getQualifiedName();
                Collection<RPsiClassMethod> elements = ClassMethodFqnIndex.getElements(qName.hashCode(), project, scope);
                elements.stream()
                        .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                        .findFirst()
                        .ifPresent(psiTarget ->
                                result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiTarget.getContainingFile(), psiTarget))
                        );
            }
        } else if (element instanceof RPsiUpperSymbol) {
            if (parent instanceof RPsiInnerModule) {
                extractRelatedExpressions(
                        element.getFirstChild(),
                        ((RPsiInnerModule) parent).getQualifiedName(),
                        result,
                        containingFile,
                        "module",
                        RPsiInnerModule.class);
            } else if (parent instanceof RPsiException) {
                extractRelatedExpressions(
                        element.getFirstChild(),
                        ((RPsiException) parent).getQualifiedName(),
                        result,
                        containingFile,
                        "exception",
                        RPsiException.class);
            }
        }
    }

    @SafeVarargs
    private <T extends PsiQualifiedNamedElement> void extractRelatedExpressions(@Nullable PsiElement element,
                                                                                @Nullable String qname,
                                                                                @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result,
                                                                                @NotNull FileBase containingFile,
                                                                                @NotNull String method,
                                                                                @NotNull Class<? extends T>... clazz) {
        if (element == null) {
            return;
        }

        FileBase psiRelatedFile = findRelatedFile(containingFile);
        if (psiRelatedFile != null) {
            List<T> expressions = psiRelatedFile.getQualifiedExpressions(qname, clazz);
            if (expressions.size() >= 1) {
                // Get latest
                T relatedElement = null;
                for (T expression : expressions) {
                    relatedElement = expression;
                }

                if (relatedElement != null) {
                    result.add(createGutterIcon(element, containingFile.isInterface(), method, psiRelatedFile, relatedElement));
                }
            }
        }
    }

    private @NotNull <T extends PsiQualifiedNamedElement> RelatedItemLineMarkerInfo<PsiElement> createGutterIcon(@NotNull PsiElement psiSource, boolean isInterface, @NotNull String method, @NotNull FileBase relatedFile, T relatedElement) {
        // GutterTooltipHelper only available for java based IDE ?
        VirtualFile virtualFile = ORFileUtils.getVirtualFile(relatedFile);
        String tooltip = "";
        if (virtualFile != null) {
            String relatedFilename = virtualFile.getName();
            tooltip = "<html><body><p>" +
                    (isInterface ? "Implements " : "Declare ") + method + " in " +
                    "<a href=\"#navigation/" + virtualFile.getPath() + ":0\"><code>" + relatedFilename + "</code></a>" +
                    "</p></body></html>";
        }

        return NavigationGutterIconBuilder.create(isInterface ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING)
                .setTooltipText(tooltip)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTargets(Collections.singleton(relatedElement))
                .createLineMarkerInfo(psiSource);
    }

    @Nullable
    public FileBase findRelatedFile(@NotNull FileBase file) {
        PsiDirectory directory = file.getParent();
        if (directory != null) {
            VirtualFile virtualFile = ORFileUtils.getVirtualFile(file);
            String filename = virtualFile == null ? "" : virtualFile.getNameWithoutExtension();

            String relatedExtension;
            if (FileHelper.isReason(file.getFileType())) {
                relatedExtension = file.isInterface() ? RmlFileType.INSTANCE.getDefaultExtension() : RmlInterfaceFileType.INSTANCE.getDefaultExtension();
            } else {
                relatedExtension = file.isInterface() ? OclFileType.INSTANCE.getDefaultExtension() : OclInterfaceFileType.INSTANCE.getDefaultExtension();
            }

            PsiFile relatedPsiFile = directory.findFile(filename + "." + relatedExtension);
            return relatedPsiFile instanceof FileBase ? (FileBase) relatedPsiFile : null;
        }
        return null;
    }
}
