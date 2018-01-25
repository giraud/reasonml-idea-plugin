package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.PsiIconUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInferredTypeUtil;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiOpen;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

// The cursor is at file level
public class FileCompletionProvider {
    public static void complete(@NotNull Project project, @NotNull FileBase file, @NotNull CompletionResultSet resultSet) {
        // Add all file expressions (local or from include)
        Collection<PsiNamedElement> fileExpressions = file.asModule().getExpressions();
        addExpressionsToResult(resultSet, fileExpressions);

        // Add all expressions from pervasives (only RmlFile ?)
        //PsiFile[]-- pervasives = FilenameIndex.getFilesByName(project, "pervasives.ml", GlobalSearchScope.allScope(project));
        //if (0 < pervasives.length) {
        //    for (PsiFile pervasive : pervasives) {
        //        if (pervasive.getVirtualFile().getCanonicalPath().endsWith("bs-platform/lib/ocaml/pervasives.ml")) {
        //            OclFile pervasivesFile = (OclFile) pervasives[0];
        //            Collection<PsiNamedElement> expressions = pervasivesFile.getExpressions();
        //            addExpressionsToResult(resultSet, expressions);
        //        }
        //    }
        //}

        // Add all expressions from the open
        // TODO complex expressions
        PsiOpen[] openExpressions = file.getOpenExpressions();
    }

    private static void addExpressionsToResult(@NotNull CompletionResultSet resultSet, Collection<PsiNamedElement> fileExpressions) {
        for (PsiNamedElement expression : fileExpressions) {
            resultSet.addElement(
                    LookupElementBuilder.create(expression).
                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
                            withTypeText(PsiInferredTypeUtil.getTypeInfo(expression))
            );
        }
    }
}
