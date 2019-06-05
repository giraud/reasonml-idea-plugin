package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PsiIconUtil;
import com.reason.Log;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class ObjectCompletionProvider {

    private static final Log LOG = Log.create("insight.object");

    private ObjectCompletionProvider() {
    }

    public static void addCompletions(@NotNull ORTypes types, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("OBJECT expression completion");

        Project project = element.getProject();
        List<String> path = new ArrayList<>();
        PsiElement previousLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        if (previousLeaf != null) {
            IElementType previousElementType = previousLeaf.getNode().getElementType();

            while (previousLeaf != null && previousElementType == types.LIDENT || previousElementType == types.SHARPSHARP || previousElementType == types.SHARP) {
                if (previousElementType == types.LIDENT) {
                    //noinspection ConstantConditions
                    LeafPsiElement node = (LeafPsiElement) previousLeaf.getNode();
                    path.add(((PsiLowerSymbol) node.getParent()).getName());
                }
                //noinspection ConstantConditions
                previousLeaf = PsiTreeUtil.prevLeaf(previousLeaf);
                previousElementType = previousLeaf == null ? null : previousLeaf.getNode().getElementType();
            }
            Collections.reverse(path);
        }

        if (path.isEmpty() || path.get(0) == null) {
            return;
        }

        PsiLet let = null;
        String lowerName = path.remove(0);

        Collection<? extends PsiQualifiedNamedElement> lets = PsiFinder.getInstance(project).findLets(lowerName, interfaceOrImplementation);

        if (!lets.isEmpty()) {
            let = (PsiLet) lets.iterator().next();
        }

        if (let == null) return;

        if (let.isRecord()) {
            Collection<PsiRecordField> fields = let.getRecordFields();
            for (PsiRecordField field : fields) {
                String name = field.getName();
                if (name != null) {
                    resultSet.addElement(LookupElementBuilder.create(name).withIcon(PsiIconUtil.getProvidersIcon(field, 0)));
                }
            }
        } else if (let.isJsObject()) {
            Collection<PsiJsObjectField> fields = let.getJsObjectFieldsForPath(path);
            for (PsiJsObjectField field : fields) {
                String name = field.getName();
                if (name != null) {
                    resultSet.addElement(LookupElementBuilder.create(name).withIcon(PsiIconUtil.getProvidersIcon(field, 0)));
                }
            }

        }

    }
}
