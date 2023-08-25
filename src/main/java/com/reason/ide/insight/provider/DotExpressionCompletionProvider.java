package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
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

    public static void addCompletions(@NotNull PsiElement element, @NotNull GlobalSearchScope searchScope, @NotNull CompletionResultSet resultSet) {
        LOG.debug("DOT expression completion");

        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof RPsiUpperSymbol) {
            // File.<caret>
            // File.Module.<caret>

            LOG.debug(" -> upper symbol", previousElement);

            RPsiUpperSymbolReference reference = (RPsiUpperSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            LOG.debug(" -> resolved to", resolvedElement);

            Collection<PsiNamedElement> expressions = new ArrayList<>();
            if (resolvedElement instanceof RPsiInnerModule) {
                addInnerModuleExpressions((RPsiInnerModule) resolvedElement, expressions, searchScope);
            } else if (resolvedElement instanceof FileBase) {
                addFileExpressions((FileBase) resolvedElement, expressions, searchScope);
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

    private static void addModuleExpressions(@Nullable PsiElement resolvedElement, @NotNull Collection<PsiNamedElement> expressions, @NotNull GlobalSearchScope scope) {
        if (resolvedElement instanceof RPsiInnerModule) {
            addInnerModuleExpressions((RPsiInnerModule) resolvedElement, expressions, scope);
        } else if (resolvedElement instanceof FileBase) {
            addFileExpressions((FileBase) resolvedElement, expressions, scope);
        } else if (resolvedElement instanceof RPsiFunctor) {
            RPsiFunctorResult returnType = ((RPsiFunctor) resolvedElement).getReturnType();
            if (returnType == null) {
                addChildren(((RPsiFunctor) resolvedElement).getBody(), expressions);
            } else {
                RPsiUpperSymbol referenceIdentifier = ORUtil.findImmediateLastChildOfClass(returnType, RPsiUpperSymbol.class);
                RPsiUpperSymbolReference reference = referenceIdentifier == null ? null : referenceIdentifier.getReference();
                PsiElement resolvedResult = reference == null ? null : reference.resolveInterface();
                if (resolvedResult != null) {
                    addModuleExpressions(resolvedResult, expressions, scope);
                }
            }
        }
    }

    private static void addFileExpressions(@NotNull FileBase file, @NotNull Collection<PsiNamedElement> expressions, @NotNull GlobalSearchScope scope) {
        Collection<String> alternativeQNames = ORModuleResolutionPsiGist.getData(file).getValues(file);
        for (String alternativeQName : alternativeQNames) {
            for (RPsiModule module : ModuleFqnIndex.getElements(alternativeQName, file.getProject(), scope)) {
                addModuleExpressions(module, expressions, scope);
            }
        }

        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiType.class));

        List<RPsiLet> lets = PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiLet.class);
        if (file.getLanguage() == RmlLanguage.INSTANCE) {
            for (RPsiLet let : lets) {
                if (!let.isPrivate()) {
                    expressions.add(let);
                }
            }
        } else {
            expressions.addAll(lets);
        }

        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiVal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiInnerModule.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiFunctor.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiClass.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiExternal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiException.class));
    }

    private static void addInnerModuleExpressions(@NotNull RPsiInnerModule module, @NotNull Collection<PsiNamedElement> expressions, @NotNull GlobalSearchScope scope) {
        if (module.getAlias() != null) {
            PsiElement resolvedAlias = ORUtil.resolveModuleSymbol(module.getAliasSymbol());
            addModuleExpressions(resolvedAlias, expressions, scope);
        } else if (module.isFunctorCall()) {
            RPsiFunctorCall functorCall = module.getFunctorCall();

            RPsiUpperSymbol referenceIdentifier = functorCall == null ? null : functorCall.getReferenceIdentifier();
            RPsiUpperSymbolReference reference = referenceIdentifier == null ? null : referenceIdentifier.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            if (resolvedElement != null) {
                addModuleExpressions(resolvedElement, expressions, scope);
            }
        } else {
            PsiElement body = module.getModuleSignature();
            if (body == null) {
                body = module.getBody();
            }
            addChildren(body, expressions);
        }
    }

    private static void addExpressions(@NotNull CompletionResultSet resultSet, @NotNull Collection<PsiNamedElement> expressions, @Nullable ORLanguageProperties language) {
        for (PsiNamedElement expression : expressions) {
            if (!(expression instanceof RPsiOpen) && !(expression instanceof RPsiInclude) && !(expression instanceof RPsiAnnotation)) {
                String name = expression.getName();
                if (name != null) {
                    String signature = RPsiSignatureUtil.getSignature(expression, language);
                    resultSet.addElement(
                            LookupElementBuilder.create(name)
                                    .withTypeText(signature)
                                    .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                }
                if (expression instanceof RPsiType eType) {
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
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiInnerModule.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiFunctor.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiClass.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiExternal.class));
        expressions.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiException.class));
    }
}
