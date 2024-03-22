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
        ORLanguageProperties langProperties = ORLanguageProperties.cast(element.getLanguage());

        if (previousElement instanceof RPsiUpperSymbol previousUpper) {
            // File.<caret>
            // File.Module.<caret>

            LOG.debug(" -> upper symbol", previousUpper);

            PsiElement resolvedElement = previousUpper.getReference().resolveInterface();
            LOG.debug(" -> resolved to", resolvedElement);

            Collection<PsiNamedElement> expressions = new ArrayList<>();
            if (resolvedElement instanceof RPsiInnerModule) {
                addInnerModuleExpressions((RPsiInnerModule) resolvedElement, expressions, searchScope);
            } else if (resolvedElement instanceof RPsiFunctor) {
                addFunctorExpressions((RPsiFunctor) resolvedElement, expressions, searchScope);
            } else if (resolvedElement instanceof FileBase) {
                addFileExpressions((FileBase) resolvedElement, expressions, searchScope);
            }

            if (expressions.isEmpty()) {
                LOG.trace(" -> no expressions found");
            } else {
                LOG.trace(" -> expressions", expressions);
                addExpressions(resultSet, expressions, langProperties);
            }
        } else if (previousElement instanceof RPsiLowerSymbol previousLower) {
            // Records: let x = {a:1, b:2};       x.<caret>
            //          let x: z = y;             x.<caret>
            //          let x = { y: { a: 1 } };  x.y.<caret>

            LOG.debug(" -> lower symbol", previousLower);

            PsiElement resolvedElement = previousLower.getReference().resolveInterface();
            if (LOG.isDebugEnabled()) {
                LOG.debug(" -> resolved to", resolvedElement == null ? null : resolvedElement.getParent());
            }

            if (resolvedElement instanceof RPsiVar resolvedVar) {
                addRecordFields(resolvedVar.getRecordFields(), langProperties, resultSet);
            } else if (resolvedElement instanceof RPsiRecordField resolvedField) {
                RPsiFieldValue fieldValue = resolvedField.getValue();
                PsiElement firstChild = fieldValue != null ? fieldValue.getFirstChild() : null;
                if (firstChild instanceof RPsiRecord recordChild) {
                    addRecordFields(recordChild.getFields(), langProperties, resultSet);
                }
            } else if (resolvedElement instanceof RPsiSignatureElement resolvedSignatureElement) {
                RPsiSignature signature = resolvedSignatureElement.getSignature();
                List<RPsiSignatureItem> items = signature != null ? signature.getItems() : null;
                if (items != null && items.size() == 1) {
                    RPsiLowerSymbol signatureSymbol = ORUtil.findImmediateLastChildOfClass(items.get(0), RPsiLowerSymbol.class);
                    PsiReference reference = signatureSymbol != null ? signatureSymbol.getReference() : null;
                    PsiElement resolve = reference != null ? reference.resolve() : null;
                    if (resolve instanceof RPsiType signatureType) {
                        addRecordFields(signatureType.getRecordFields(), langProperties, resultSet);
                    }
                }
            }
        }
    }

    private static void addRecordFields(@Nullable Collection<RPsiRecordField> recordFields, @Nullable ORLanguageProperties langProperties, @NotNull CompletionResultSet resultSet) {
        if (recordFields != null) {
            for (RPsiRecordField recordField : recordFields) {
                resultSet.addElement(
                        LookupElementBuilder.create(recordField)
                                .withTypeText(RPsiSignatureUtil.getSignature(recordField, langProperties))
                                .withIcon(PsiIconUtil.getProvidersIcon(recordField, 0)));
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
                RPsiUpperSymbol referenceIdentifier = returnType.getModuleType();
                PsiElement resolvedResult = referenceIdentifier == null ? null : referenceIdentifier.getReference().resolveInterface();
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

    private static void addFunctorExpressions(@NotNull RPsiFunctor functor, @NotNull Collection<PsiNamedElement> expressions, @NotNull GlobalSearchScope scope) {
        RPsiFunctorResult returnType = functor.getReturnType();
        if (returnType == null) {
            PsiElement functorBody = functor.getBody();
            if (functorBody != null) {
                addChildren(functorBody, expressions);
            }
        } else {
            RPsiUpperSymbol moduleType = returnType.getModuleType();
            PsiElement resolvedReturnType = moduleType != null ? moduleType.getReference().resolveInterface() : null;
            if (resolvedReturnType instanceof RPsiInnerModule resolvedReturnModuleType) {
                addInnerModuleExpressions(resolvedReturnModuleType, expressions, scope);
            }
        }
    }

    private static void addInnerModuleExpressions(@NotNull RPsiInnerModule module, @NotNull Collection<PsiNamedElement> expressions, @NotNull GlobalSearchScope scope) {
        if (module.getAlias() != null) { // use gist alternate names ?
            PsiElement resolvedAlias = ORUtil.resolveModuleSymbol(module.getAliasSymbol());
            addModuleExpressions(resolvedAlias, expressions, scope);
        } else if (module.isFunctorCall()) {
            RPsiFunctorCall functorCall = module.getFunctorCall();

            RPsiUpperSymbol referenceIdentifier = functorCall == null ? null : functorCall.getReferenceIdentifier();
            ORPsiUpperSymbolReference reference = referenceIdentifier == null ? null : referenceIdentifier.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            if (resolvedElement != null) {
                addModuleExpressions(resolvedElement, expressions, scope);
            }
        } else if (module.getUnpack() != null) {
            RPsiLowerSymbol firstClassSymbol = module.getUnpack().getFirstClassSymbol();
            ORPsiLowerSymbolReference symbolReferenceIdentifier = firstClassSymbol != null ? firstClassSymbol.getReference() : null;
            PsiElement resolvedFirstClassSymbol = symbolReferenceIdentifier != null ? symbolReferenceIdentifier.resolve() : null;
            if (resolvedFirstClassSymbol instanceof RPsiSignatureElement signatureElement) {
                RPsiSignature signature = signatureElement.getSignature();
                if (signature instanceof RPsiModuleSignature firstClassModuleSignature) {
                    RPsiUpperSymbol moduleReferenceIdentifier = firstClassModuleSignature.getNameIdentifier();
                    ORPsiUpperSymbolReference moduleReference = moduleReferenceIdentifier != null ? moduleReferenceIdentifier.getReference() : null;
                    PsiElement resolvedFirstClassModule = moduleReference != null ? moduleReference.resolve() : null;
                    if (resolvedFirstClassModule != null) {
                        addModuleExpressions(resolvedFirstClassModule, expressions, scope);
                    }
                }
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
        List<RPsiInclude> includes = PsiTreeUtil.getStubChildrenOfTypeAsList(body, RPsiInclude.class);
        for (RPsiInclude include : includes) {
            RPsiUpperSymbol moduleSymbol = ORUtil.findImmediateLastChildOfClass(include, RPsiUpperSymbol.class);
            ORPsiUpperSymbolReference reference = moduleSymbol != null ? moduleSymbol.getReference() : null;
            PsiElement resolvedResult = reference != null ? reference.resolveInterface() : null;
            if (resolvedResult instanceof RPsiModule resolvedModule) {
                PsiElement resolvedBody = resolvedModule instanceof RPsiInnerModule ? ((RPsiInnerModule) resolvedModule).getModuleSignature() : null;
                if (resolvedBody == null) {
                    resolvedBody = resolvedModule.getBody();
                }
                addChildren(resolvedBody, expressions);
            }
        }

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
