package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.navigation.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import icons.*;
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
        if (parent instanceof PsiDeconstruction) {
            parent = parent.getParent();
        }

        Project project = element.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        FileBase containingFile = (FileBase) element.getContainingFile();
        boolean isInterface = containingFile.isInterface();

        if (element instanceof PsiLowerIdentifier) {
            if (parent instanceof PsiLet) {
                String qNameLet = ((PsiLetImpl) parent).getQualifiedName();
                if (((PsiLet) parent).isDeconstruction()) {
                    qNameLet = Joiner.join(".", ORUtil.getQualifiedPath(parent)) + "." + element.getText();
                }
                final String qName = qNameLet;

                Collection<PsiVal> vals = ValFqnIndex.getElements(qName.hashCode(), project, scope);
                vals.stream()
                        .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                        .findFirst()
                        .ifPresentOrElse(psiVal ->
                                        result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiVal.getContainingFile(), psiVal))
                                , () -> {
                                    Collection<PsiLet> lets = LetFqnIndex.getElements(qName.hashCode(), project, scope);
                                    lets.stream()
                                            .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                                            .findFirst()
                                            .ifPresent(psiLet ->
                                                    result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiLet.getContainingFile(), psiLet))
                                            );
                                });
            } else if (parent instanceof PsiExternal) {
                String externalQName = ((PsiExternalImpl) parent).getQualifiedName();
                Collection<PsiExternal> elements = ExternalFqnIndex.getElements(externalQName.hashCode(), project, scope);
                elements.stream()
                        .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                        .findFirst()
                        .ifPresent(psiTarget ->
                                result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiTarget.getContainingFile(), psiTarget))
                        );
            } else if (parent instanceof PsiValImpl) {
                String valQName = ((PsiValImpl) parent).getQualifiedName();
                Collection<PsiLet> elements = LetFqnIndex.getElements(valQName.hashCode(), project, scope);
                elements.stream()
                        .filter(PSI_IMPL_PREDICATE)
                        .findFirst()
                        .ifPresent(psiTarget ->
                                result.add(createGutterIcon(element, isInterface, "method", (FileBase) psiTarget.getContainingFile(), psiTarget))
                        );
            } else {
                if (parent instanceof PsiType) {
                    String valQName = ((PsiTypeImpl) parent).getQualifiedName();
                    Collection<PsiType> elements = TypeFqnIndex.getElements(valQName.hashCode(), project, scope);
                    elements.stream()
                            .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                            .findFirst()
                            .ifPresent(psiTarget ->
                                    result.add(createGutterIcon(element, isInterface, "type", (FileBase) psiTarget.getContainingFile(), psiTarget))
                            );
                } else if (parent instanceof PsiKlass) {
                    String qName = ((PsiKlassImpl) parent).getQualifiedName();
                    Collection<PsiKlass> elements = KlassFqnIndex.getElements(qName.hashCode(), project, scope);
                    elements.stream()
                            .filter(isInterface ? PSI_IMPL_PREDICATE : PSI_INTF_PREDICATE)
                            .findFirst()
                            .ifPresent(psiTarget ->
                                    result.add(createGutterIcon(element, isInterface, "class", (FileBase) psiTarget.getContainingFile(), psiTarget))
                            );
                }
            }
        } else if (element instanceof PsiUpperIdentifier) {
            if (parent instanceof PsiInnerModule) {
                extractRelatedExpressions(
                        element.getFirstChild(),
                        ((PsiInnerModule) parent).getQualifiedName(),
                        result,
                        containingFile,
                        "module",
                        PsiInnerModule.class);
            } else if (parent instanceof PsiException) {
                extractRelatedExpressions(
                        element.getFirstChild(),
                        ((PsiException) parent).getQualifiedName(),
                        result,
                        containingFile,
                        "exception",
                        PsiException.class);
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
        // GutterTooltipHelper only available for java-based IDE ?
        String relatedFilename = relatedFile.getVirtualFile().getName();
        String tooltip = "<html><body>" +
                "<p>" + (isInterface ? "Implements " : "Declare ") + method + " in <a href=\"#navigation/" + relatedFile.getVirtualFile().getPath() + ":0\"><code>" + relatedFilename + "</code></a></p>" +
                "</body></html>";

        return NavigationGutterIconBuilder.create(isInterface ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING)
                .setTooltipText(tooltip)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTargets(Collections.singleton(relatedElement))
                .createLineMarkerInfo(psiSource instanceof PsiLowerIdentifier ? psiSource.getFirstChild() : psiSource);
    }

    @Nullable
    public FileBase findRelatedFile(@NotNull FileBase file) {
        PsiDirectory directory = file.getParent();
        if (directory != null) {
            String filename = file.getVirtualFile().getNameWithoutExtension();

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
