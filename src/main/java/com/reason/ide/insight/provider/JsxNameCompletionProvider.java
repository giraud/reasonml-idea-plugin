package com.reason.ide.insight.provider;

import com.intellij.codeInsight.*;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.openapi.application.ApplicationManager.*;
import static com.intellij.util.PsiIconUtil.*;

public class JsxNameCompletionProvider {
    private static final Log LOG = Log.create("insight.jsx.name");

    private JsxNameCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull ORLangTypes types, @NotNull GlobalSearchScope scope, @NotNull CompletionResultSet resultSet) {
        LOG.debug("JSX name expression completion");

        Collection<RPsiModule> expressions = new ArrayList<>();
        Project project = element.getProject();
        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);

        if (prevLeaf != null && prevLeaf.getNode().getElementType() == types.DOT) {
            // Inner component completion
            PsiElement previousElement = prevLeaf.getPrevSibling();
            if (previousElement instanceof RPsiUpperSymbol) {
                ORPsiUpperSymbolReference reference = (ORPsiUpperSymbolReference) previousElement.getReference();
                PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
                LOG.debug(" -> resolved to", resolvedElement);

                // A component is resolved to the make function
                if (resolvedElement != null) {
                    PsiElement resolvedModule = PsiTreeUtil.getStubOrPsiParentOfType(resolvedElement, RPsiModule.class);
                    if (resolvedModule == null) {
                        resolvedModule = resolvedElement.getContainingFile();
                    }

                    for (RPsiModule module : PsiTreeUtil.getStubChildrenOfTypeAsList(resolvedModule, RPsiModule.class)) {
                        if (module.isComponent()) {
                            expressions.add(module);
                        }
                    }
                }
            }
        } else {
            // List inner components above
            List<RPsiInnerModule> localModules = ORUtil.findPreviousSiblingsOrParentOfClass(element, RPsiInnerModule.class);
            for (RPsiInnerModule localModule : localModules) {
                if (localModule.isComponent() && !localModule.isModuleType()) {
                    expressions.add(localModule);
                }
            }

            // List all top level components
            final RPsiModule currentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            String currentModuleName = currentModule == null ? "" : currentModule.getModuleName();
            getApplication().getService(FileModuleIndexService.class).getTopModules(project, scope)
                    .forEach(data -> {
                        if ((data.isComponent() || data.hasComponents()) && !data.getModuleName().equals(currentModuleName)) {
                            resultSet.addElement(LookupElementBuilder.create(data.getModuleName())
                                    .withIcon(IconProvider.getDataModuleIcon(data))
                                    .withInsertHandler((context, item) -> insertTagNameHandler(project, context, data.getModuleName())));
                        }
                    });
        }

        for (RPsiModule expression : expressions) {
            String componentName = expression instanceof FileBase ? ((FileBase) expression).getModuleName() : expression.getName();
            if (componentName != null) {
                resultSet.addElement(LookupElementBuilder.create(componentName)
                        .withIcon(getIconFromProviders(expression, 0))
                        .withInsertHandler((context, item) -> insertTagNameHandler(project, context, componentName)));
            }
        }
    }

    private static void insertTagNameHandler(@NotNull Project project, @NotNull InsertionContext context, @NotNull String tagName) {
        char completionChar = context.getCompletionChar();
        if (completionChar == ' ') {
            context.setAddCompletionChar(false);
        }

        boolean closeTag = false;
        CharSequence chars = context.getDocument().getCharsSequence();

        int tagPrefixOffset = context.getStartOffset() - 2;
        if (tagPrefixOffset >= 0) {
            CharSequence tagPrefix = chars.subSequence(tagPrefixOffset, context.getStartOffset());
            closeTag = "</".contentEquals(tagPrefix);
        }

        Editor editor = context.getEditor();
        if (closeTag) {
            int tailOffset = context.getTailOffset();
            if (tailOffset < chars.length() && chars.charAt(tailOffset) != '>') {
                EditorModificationUtil.insertStringAtCaret(editor, ">");
            }
        } else {
            EditorModificationUtil.insertStringAtCaret(editor, " ></" + tagName + ">");
            editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 4 - tagName.length());
            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
        }
    }
}
