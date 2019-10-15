package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.ide.insight.provider.JsxAttributeCompletionProvider;
import com.reason.ide.insight.provider.JsxNameCompletionProvider;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.reason.RmlQNameFinder;
import com.reason.lang.reason.RmlTypes;

public class RmlCompletionContributor extends CompletionContributor {

    RmlCompletionContributor() {
        super(RmlTypes.INSTANCE, new RmlQNameFinder());
    }

    @Override
    protected boolean addSpecificCompletions(ORTypes types, PsiElement element, PsiElement parent, PsiElement grandParent, CompletionResultSet result) {
        IElementType elementType = element.getNode().getElementType();

        if (elementType == types.TAG_NAME) {
            LOG.debug("Previous element type is TAG_NAME");
            JsxNameCompletionProvider.addCompletions(element, result);
            return true;
        }

        if (parent instanceof PsiTagProperty /*inside the prop name*/ || parent instanceof PsiTagStart || grandParent instanceof PsiTagStart) {
            LOG.debug("Inside a Tag start");
            JsxAttributeCompletionProvider.addCompletions(element, result);
            return true;
        }

        return false;
    }

}
