package com.reason.ide.testAssistant;

import static com.intellij.notification.NotificationType.INFORMATION;

import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.awt.RelativePoint;
import jpsplugin.com.reason.ORNotification;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.psi.PsiModule;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class GotoTestDataAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            DataContext dataContext = e.getDataContext();
            List<String> fileNames = findRelatedFiles(project, dataContext);
            if (fileNames.isEmpty()) {
                Notifications.Bus.notify(
                        new ORNotification("testdata", "Found no testdata files", "Cannot find related files", INFORMATION, null),
                        project);
                return;
            }

            Editor editor = e.getData(CommonDataKeys.EDITOR);
            JBPopupFactory popupFactory = JBPopupFactory.getInstance();
            RelativePoint point =
                    editor == null
                            ? popupFactory.guessBestPopupLocation(dataContext)
                            : popupFactory.guessBestPopupLocation(editor);

            TestDataNavigationHandler.navigate(point, fileNames, project);
        }
    }

    @NotNull
    private List<String> findRelatedFiles(@NotNull Project project, @NotNull DataContext context) {
        PsiFile file = context.getData(CommonDataKeys.PSI_FILE);
        if (file instanceof FileBase) {
            VirtualFile relatedFile;

            GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
            PsiFinder psiFinder = project.getService(PsiFinder.class);
            PsiModule relatedModule;

            String[] tokens = splitModuleName(((FileBase) file).getModuleName());
            if (tokens.length == 1) {
                Set<PsiModule> relatedModules =
                        psiFinder.findModulesbyName(
                                tokens[0] + "_test",
                                ORFileType.implementationOnly,
                                module -> module instanceof FileBase
                        );
                relatedModule = relatedModules.isEmpty() ? null : relatedModules.iterator().next();
                if (relatedModule == null) {
                    relatedModules =
                            psiFinder.findModulesbyName(
                                    tokens[0] + "_spec",
                                    ORFileType.implementationOnly,
                                    module -> module instanceof FileBase
                            );
                    relatedModule = relatedModules.isEmpty() ? null : relatedModules.iterator().next();
                }
            } else {
                Set<PsiModule> relatedModules =
                        psiFinder.findModulesbyName(
                                tokens[0],
                                ORFileType.implementationOnly,
                                module -> module instanceof FileBase
                        );
                relatedModule = relatedModules.isEmpty() ? null : relatedModules.iterator().next();
            }

            if (relatedModule != null) {
                return Collections.singletonList(((FileBase) relatedModule).getVirtualFile().getPath());
            }
        }

        return Collections.emptyList();
    }

    private @NotNull String[] splitModuleName(@NotNull String moduleName) {
        int underscoreIndex = moduleName.lastIndexOf("_");
        return 0 < underscoreIndex
                ? new String[]{
                moduleName.substring(0, underscoreIndex), moduleName.substring(underscoreIndex + 1)
        }
                : new String[]{moduleName};
    }
}
