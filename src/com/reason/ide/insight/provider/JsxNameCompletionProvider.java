package com.reason.ide.insight.provider;

import com.intellij.codeInsight.*;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.util.PsiIconUtil.*;

public class JsxNameCompletionProvider {
    private static final Log LOG = Log.create("insight.jsx.name");

    private JsxNameCompletionProvider() {
    }

    public static void addCompletions(@NotNull ORTypes types, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("JSX name expression completion");

        Collection<PsiNamedElement> expressions = new ArrayList<>();
        Project project = element.getProject();
        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);

        if (prevLeaf != null && prevLeaf.getNode().getElementType() == types.DOT) {
            // Inner component completion
            PsiElement previousElement = prevLeaf.getPrevSibling();
            if (previousElement instanceof PsiUpperSymbol) {
                PsiUpperSymbolReference reference = (PsiUpperSymbolReference) previousElement.getReference();
                PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
                LOG.debug(" -> resolved to", resolvedElement);

                // A component is resolved to the make function
                if (resolvedElement != null) {
                    PsiElement resolvedModule = PsiTreeUtil.getStubOrPsiParentOfType(resolvedElement, PsiModule.class);
                    if (resolvedModule == null) {
                        resolvedModule = resolvedElement.getContainingFile();
                    }

                    for (PsiModule module : PsiTreeUtil.getStubChildrenOfTypeAsList(resolvedModule, PsiModule.class)) {
                        if (module.isComponent() && !(module instanceof PsiFakeModule)) {
                            expressions.add(module);
                        }
                    }
                }
            }
        } else {
            // List inner components above
            List<PsiModule> localModules = ORUtil.findPreviousSiblingsOrParentOfClass(element, PsiModule.class);
            for (PsiModule localModule : localModules) {
                if (localModule.isComponent() && !localModule.isInterface()) {
                    expressions.add(localModule);
                }
            }

            // List all top level components
            final PsiModule currentModule = getParentModule(element);
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            ModuleComponentIndex.processItems(project, scope, componentModule -> {
                if (componentModule instanceof PsiFakeModule && !componentModule.equals(currentModule)) {
                    expressions.add(componentModule);
                }
            });
        }

        for (PsiNamedElement expression : expressions) {
            String componentName = expression.getName();
            if (componentName != null) {
                resultSet.addElement(LookupElementBuilder.create(componentName)
                        .withIcon(getProvidersIcon(expression, 0))
                        .withInsertHandler((context, item) -> insertTagNameHandler(project, context, componentName)));
            }
        }
    }

    private static @Nullable PsiModule getParentModule(@NotNull PsiElement element) {
        PsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, PsiModule.class);
        if (parentModule == null) {
            PsiElement lastElement = element.getContainingFile().getLastChild();
            if (lastElement instanceof PsiFakeModule) {
                parentModule = (PsiModule) lastElement;
            }
        }
        return parentModule;
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
            if (chars.charAt(context.getTailOffset()) != '>') {
                EditorModificationUtil.insertStringAtCaret(editor, ">");
            }
        } else {
            EditorModificationUtil.insertStringAtCaret(editor, " ></" + tagName + ">");
            editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 4 - tagName.length());
            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
        }
    }
}
