package com.reason.ide.search;

import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORImplementationSearch extends QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters> {
    private static final Log LOG = Log.create("search");

    @Override
    public void processQuery(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiElement> consumer) {
        PsiElement source = queryParameters.getElement();
        if (!source.isValid()) {
            return;
        }

        SearchScope scope = queryParameters.getScope();
        if (scope instanceof GlobalSearchScope searchScope) {
            ReadAction.run(() -> {
                Project project = queryParameters.getProject();
                if (source instanceof RPsiVal valSource) {
                    String qName = valSource.getQualifiedName();
                    if (qName != null) {
                        LOG.debug("Process implementation search for VAL", qName, scope, project);
                        Collection<RPsiLet> elements = LetFqnIndex.getElements(qName, project, searchScope);
                        for (RPsiLet element : elements) {
                            System.out.println("Processing " + element.getQualifiedName() + " " + ORFileUtils.getVirtualFile(element.getContainingFile()));
                            consumer.process(element);
                        }
                    }
                }
            });
        }
    }
}

