package com.reason.ide.search;

import com.intellij.openapi.application.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.*;
import com.intellij.util.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORImplementationSearch extends QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters> {
    private static final Log LOG = Log.create("search");

    @Override
    public void processQuery(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiElement> consumer) {
        LOG.debug("processQuery", queryParameters.getProject());
        //LOG.debug("processQuery", queryParameters.getElement());
        //System.out.println(queryParameters.getScope());

        PsiElement source = queryParameters.getElement();
        if (!source.isValid()) {
            return;
        }

        SearchScope scope = queryParameters.getScope();
        if (scope instanceof GlobalSearchScope searchScope) {
            ReadAction.run(() -> {
                if (source instanceof RPsiVar varSource) {
                    String qName = varSource.getQualifiedName();
                    if (qName != null) {
                        System.out.println("Process implementation search for VAR " + qName + " with scope " + scope);
                        Collection<RPsiLet> elements = LetFqnIndex.getElements(qName, queryParameters.getProject(), searchScope);
                        for (RPsiLet element : elements) {
                            System.out.println("Processing " + element.getQualifiedName() + " " + element.getContainingFile().getVirtualFile());
                            consumer.process(element);
                        }
                    }
                } else if (source instanceof RPsiUpperSymbol upperSymbolSource) {
                    System.out.println("Process implementation search for UPPER " + upperSymbolSource.getName() + " with scope " + scope);
                }
            });
        }
    }
}
