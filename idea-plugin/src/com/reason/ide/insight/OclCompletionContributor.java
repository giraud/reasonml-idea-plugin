package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclQNameFinder;
import com.reason.lang.ocaml.OclTypes;

public class OclCompletionContributor extends CompletionContributor {

    OclCompletionContributor() {
        super(OclTypes.INSTANCE, OclQNameFinder.INSTANCE);
    }

    @Override
    protected boolean addSpecificCompletions(ORTypes types, PsiElement element, PsiElement parent, PsiElement grandParent, CompletionResultSet result) {
        return false;
    }

}
