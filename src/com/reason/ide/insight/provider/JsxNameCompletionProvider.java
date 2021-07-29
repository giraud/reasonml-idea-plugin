package com.reason.ide.insight.provider;

import com.intellij.codeInsight.*;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.psi.search.GlobalSearchScope.*;
import static com.intellij.util.PsiIconUtil.*;

public class JsxNameCompletionProvider {
    private static final Log LOG = Log.create("insight.jsx.name");

    private JsxNameCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("JSX name expression completion");

        FileBase originalFile = (FileBase) element.getContainingFile();
        Project project = originalFile.getProject();
        GlobalSearchScope scope = allScope(project);

        Collection<PsiModule> modules = PsiFinder.getInstance(project).findComponents(scope);
        LOG.debug(" -> Modules found", modules);

        for (PsiModule module : modules) {
            String moduleName = module.getModuleName();
            if (moduleName != null) {
                FileBase containingFile = module instanceof FileBase ? (FileBase) module : (FileBase) module.getContainingFile();
                // if component is the file, don't add it to the completion result
                if (!module.getQualifiedName().equals(originalFile.getModuleName())) {
                    resultSet.addElement(
                            LookupElementBuilder.create(moduleName)
                                    .withIcon(getProvidersIcon(module, 0))
                                    .withTypeText(
                                            module instanceof PsiInnerModule
                                                    ? containingFile.getModuleName()
                                                    : containingFile.shortLocation())
                                    .withInsertHandler(
                                            (context, item) -> insertTagNameHandler(project, context, moduleName)));
                }
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
