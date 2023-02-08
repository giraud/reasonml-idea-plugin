package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.RPsiAnnotation;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
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

        if (previousElement instanceof RPsiUpperSymbol) {
            // File.<caret>
            // File.Module.<caret>

            LOG.debug(" -> upper symbol", previousElement);

            PsiUpperSymbolReference reference = (PsiUpperSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            LOG.debug(" -> resolved to", resolvedElement);

            Collection<PsiNamedElement> expressions = new ArrayList<>();
            if (resolvedElement instanceof RPsiInnerModule) {
                addInnerModuleExpressions((RPsiInnerModule) resolvedElement, expressions);
            } else if (resolvedElement instanceof FileBase) {
                addFileExpressions((FileBase) resolvedElement, expressions);
            }

            if (expressions.isEmpty()) {
                LOG.trace(" -> no expressions found");
            } else {
                LOG.trace(" -> expressions", expressions);
                addExpressions(resultSet, expressions, ORLanguageProperties.cast(element.getLanguage()));
            }
        } else if (previousElement instanceof RPsiLowerSymbol) {
            // Records: let x = {a:1, b:2};       x.<caret>
            //          let x: z = y;             x.<caret>
            //          let x = { y: { a: 1 } };  x.y.<caret>

            LOG.debug(" -> lower symbol", previousElement);

            PsiLowerSymbolReference reference = (PsiLowerSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            if (LOG.isDebugEnabled()) {
                LOG.debug(" -> resolved to", resolvedElement == null ? null : resolvedElement.getParent());
            }

            if (resolvedElement instanceof RPsiVar) {
                for (RPsiRecordField recordField : ((RPsiVar) resolvedElement).getRecordFields()) {
                    resultSet.addElement(
                            LookupElementBuilder.create(recordField)
                                    .withTypeText(RPsiSignatureUtil.getSignature(recordField, ORLanguageProperties.cast(element.getLanguage())))
                                    .withIcon(PsiIconUtil.getProvidersIcon(recordField, 0)));
                }
            }
        }
    }

    private static void addModuleExpressions(@Nullable PsiElement resolvedElement, @NotNull Collection<PsiNamedElement> expressions) {
        if (resolvedElement instanceof RPsiInnerModule) {
            addInnerModuleExpressions((RPsiInnerModule) resolvedElement, expressions);
        } else if (resolvedElement instanceof FileBase) {
            addFileExpressions((FileBase) resolvedElement, expressions);
        } else if (resolvedElement instanceof RPsiFunctor) {
            RPsiFunctorResult returnType = ((RPsiFunctor) resolvedElement).getReturnType();
            if (returnType == null) {
                addChildren(((RPsiFunctor) resolvedElement).getBody(), expressions);
            } else {
                addModuleExpressions(returnType.resolveModule(), expressions);
            }
        }
    }

    private static void addFileExpressions(@NotNull FileBase element, @NotNull Collection<PsiNamedElement> expressions) {
        List<RPsiInclude> includes = PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiInclude.class);
        for (RPsiInclude include : includes) {
            addModuleExpressions(include.resolveModule(), expressions);
        }

        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiType.class));

        List<RPsiLet> lets = PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiLet.class);
        if (element.getLanguage() == RmlLanguage.INSTANCE) {
            for (RPsiLet let : lets) {
                if (!let.isPrivate()) {
                    expressions.add(let);
                }
            }
        } else {
            expressions.addAll(lets);
        }

        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiVal.class));
        List<RPsiModule> modules = PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiModule.class);
        modules.remove(modules.size() - 1); // remove fake module
        expressions.addAll(modules);
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiFunctor.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiClass.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiExternal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiException.class));
    }

    private static void addInnerModuleExpressions(@NotNull RPsiInnerModule module, @NotNull Collection<PsiNamedElement> expressions) {
        if (module.getAlias() != null) {
            PsiElement resolvedAlias = ORUtil.resolveModuleSymbol(module.getAliasSymbol());
            addModuleExpressions(resolvedAlias, expressions);
        } else if (module.isFunctorCall()) {
            RPsiFunctorCall functorCall = module.getFunctorCall();
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

    private static void addExpressions(@NotNull CompletionResultSet resultSet, @NotNull Collection<PsiNamedElement> expressions, @Nullable ORLanguageProperties language) {
        for (PsiNamedElement expression : expressions) {
            if (!(expression instanceof RPsiOpen) && !(expression instanceof RPsiInclude) && !(expression instanceof RPsiAnnotation)) {
                // TODO: if include => include
                String name = expression.getName();
                if (name != null) {
                    String signature = RPsiSignatureUtil.getSignature(expression, language);
                    resultSet.addElement(
                            LookupElementBuilder.create(name)
                                    .withTypeText(signature)
                                    .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                }
                if (expression instanceof RPsiType) {
                    RPsiType eType = (RPsiType) expression;
                    Collection<RPsiVariantDeclaration> variants = eType.getVariants();
                    if (!variants.isEmpty()) {
                        for (RPsiVariantDeclaration variant : variants) {
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

    private static void addChildren(@Nullable PsiElement body, @NotNull Collection<PsiNamedElement> expressions) {
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiType.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiLet.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiVal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiModule.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiFunctor.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiClass.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiExternal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiException.class));
    }
}
