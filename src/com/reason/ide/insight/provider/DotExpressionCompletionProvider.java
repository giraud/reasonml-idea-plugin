package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.signature.*;
import com.reason.lang.reason.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class DotExpressionCompletionProvider {
    private static final Log LOG = Log.create("insight.dot");

    private DotExpressionCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("DOT expression completion");

        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof PsiUpperSymbol) {
            // File.<caret>
            // File.Module.<caret>

            LOG.debug(" -> upper symbol", previousElement);

            PsiUpperSymbolReference reference = (PsiUpperSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            LOG.debug(" -> resolved to", resolvedElement);

            Collection<PsiNamedElement> expressions = new ArrayList<>();
            if (resolvedElement instanceof PsiUpperIdentifier) {
                PsiElement resolvedParent = resolvedElement.getParent();
                if (resolvedParent instanceof PsiInnerModule) {
                    addInnerModuleExpressions((PsiInnerModule) resolvedParent, expressions);
                }
            } else if (resolvedElement instanceof FileBase) {
                addFileExpressions((FileBase) resolvedElement, expressions);
            }

            if (expressions.isEmpty()) {
                LOG.trace(" -> no expressions found");
            } else {
                LOG.trace(" -> expressions", expressions);
                addExpressions(resultSet, expressions, ORLanguageProperties.cast(element.getLanguage()));
            }
        } else if (previousElement instanceof PsiLowerSymbol) {
            // Records: let x = {a:1, b:2};       x.<caret>
            //          let x: z = y;             x.<caret>
            //          let x = { y: { a: 1 } };  x.y.<caret>

            LOG.debug(" -> lower symbol", previousElement);

            PsiLowerSymbolReference reference = (PsiLowerSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            if (LOG.isDebugEnabled()) {
                LOG.debug(" -> resolved to", resolvedElement == null ? null : resolvedElement.getParent());
            }

            if (resolvedElement instanceof PsiLowerIdentifier) {
                PsiElement resolvedParent = resolvedElement.getParent();
                if (resolvedParent instanceof PsiVar) {
                    for (PsiRecordField recordField : ((PsiVar) resolvedParent).getRecordFields()) {
                        resultSet.addElement(
                                LookupElementBuilder.create(recordField)
                                        .withTypeText(PsiSignatureUtil.getSignature(recordField, ORLanguageProperties.cast(element.getLanguage())))
                                        .withIcon(PsiIconUtil.getProvidersIcon(recordField, 0)));
                    }
                }
            }
        }
    }

    private static void addModuleExpressions(@Nullable PsiElement resolvedElement, @NotNull Collection<PsiNamedElement> expressions) {
        if (resolvedElement instanceof PsiInnerModule) {
            addInnerModuleExpressions((PsiInnerModule) resolvedElement, expressions);
        } else if (resolvedElement instanceof FileBase) {
            addFileExpressions((FileBase) resolvedElement, expressions);
        } else if (resolvedElement instanceof PsiFunctor) {
            PsiFunctorResult returnType = ((PsiFunctor) resolvedElement).getReturnType();
            if (returnType == null) {
                addChildren(((PsiFunctor) resolvedElement).getBody(), expressions);
            } else {
                addModuleExpressions(returnType.resolveModule(), expressions);
            }
        }
    }

    private static void addFileExpressions(@NotNull FileBase element, @NotNull Collection<PsiNamedElement> expressions) {
        List<PsiInclude> includes = PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiInclude.class);
        for (PsiInclude include : includes) {
            addModuleExpressions(include.resolveModule(), expressions);
        }

        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiType.class));

        List<PsiLet> lets = PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiLet.class);
        if (element.getLanguage() == RmlLanguage.INSTANCE) {
            for (PsiLet let : lets) {
                if (!let.isPrivate()) {
                    expressions.add(let);
                }
            }
        } else {
            expressions.addAll(lets);
        }

        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiVal.class));
        List<PsiModule> modules = PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiModule.class);
        // remove fake module
        modules.remove(modules.size() - 1);
        expressions.addAll(modules);
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiFunctor.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiClass.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiExternal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, PsiException.class));
    }

    private static void addInnerModuleExpressions(@NotNull PsiInnerModule module, @NotNull Collection<PsiNamedElement> expressions) {
        if (module.getAlias() != null) {
            PsiElement resolvedAlias = ORUtil.resolveModuleSymbol(module.getAliasSymbol());
            addModuleExpressions(resolvedAlias, expressions);
        } else if (module.isFunctorCall()) {
            PsiFunctorCall functorCall = module.getFunctorCall();
            if (functorCall != null) {
                addModuleExpressions(functorCall.resolveFunctor(), expressions);
            }
        } else {
            PsiElement body = module.getModuleType();
            if (body == null) {
                body = module.getBody();
            }
            addChildren(body, expressions);
        }
    }

    private static void addChildren(@Nullable PsiElement body, @NotNull Collection<PsiNamedElement> expressions) {
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiType.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiLet.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiVal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiModule.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiFunctor.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiClass.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiExternal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiException.class));
    }

    private static void addExpressions(@NotNull CompletionResultSet resultSet, @NotNull Collection<PsiNamedElement> expressions, @Nullable ORLanguageProperties language) {
        for (PsiNamedElement expression : expressions) {
            if (!(expression instanceof PsiOpen) && !(expression instanceof PsiInclude) && !(expression instanceof PsiAnnotation)) {
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
