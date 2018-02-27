package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.reason.icons.Icons;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class JsxAttributeCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        //System.out.println("»» JSX attribute completion");

        Project project = parameters.getOriginalFile().getProject();
        PsiElement parent = parameters.getOriginalPosition().getParent();

        if (parent instanceof PsiTagStart) {
            PsiTagStart tag = (PsiTagStart) parent;
            Map<String, String> attributes = tag.getAttributes();

            // TODO: attributes for LowerSymbol tag : external props in ReactDOMRe  => PsiExternal(name=props) => PsiSignature => all named elements
            Collection<PsiModule> reactModules = StubIndex.getElements(IndexKeys.MODULES, "ReactDOMRe", project, GlobalSearchScope.allScope(project), PsiModule.class);
            if (!reactModules.isEmpty()) {
                PsiModule reactDomRe = reactModules.iterator().next();
                PsiExternal props = reactDomRe.getExternalExpression("props");
                if (props != null) {
                    PsiSignature signature = PsiTreeUtil.getStubChildOfType(props, PsiSignature.class);
                    if (signature != null) {
                        System.out.println(signature);
                    }
                }
            }

            // TODO: additional attributes for UpperSymbol => only key and ref
            attributes.put("key", "string=?");
            attributes.put("ref", "Js.nullable(Dom.element) => unit=?");

            // Attributes already used
            Collection<PsiTagProperty> usedAttributes = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
            List<String> usedNames = usedAttributes.stream().map(PsiElement::getText).collect(toList());

            // Now populate the dialog
            for (Map.Entry<String, String> attributeEntry : attributes.entrySet()) {
                String attributeName = attributeEntry.getKey();
                if (!usedNames.contains(attributeName)) {
                    resultSet.addElement(LookupElementBuilder.create(attributeName).
                            withTypeText(attributeEntry.getValue(), true).
                            withIcon(Icons.ATTRIBUTE).
                            withInsertHandler((context, item) -> insertTagAttributeHandler(context))
                    );
                }
            }
        }
    }

    private static void insertTagAttributeHandler(InsertionContext context) {
        context.setAddCompletionChar(false);

        Editor editor = context.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, "=()");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
    }
}
