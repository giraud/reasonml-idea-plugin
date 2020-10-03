package com.reason.ide.console;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class BsToolWindowFactory extends ORToolWindowFactory {

  public static final String ID = "BuckleScript:";

  @Override
  public @NotNull String getId() {
    return ID;
  }

  @Override
  public @NotNull Icon getIcon() {
    return ORIcons.BUCKLESCRIPT_TOOL;
  }

  @Nls
  @Override
  public @NotNull String getTitle() {
    return "Process";
  }

  @Override
  public @NotNull String getStripeTitle() {
    return "BuckleScript";
  }

  @Override
  public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window) {
    SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

    BsConsole console = new BsConsole(project);
    panel.setContent(console.getComponent());

    ActionToolbar toolbar = createToolbar(console);
    panel.setToolbar(toolbar.getComponent());

    Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

    window.getContentManager().addContent(content);

    Disposer.register(window.getDisposable(), console);
  }

  @NotNull
  private ActionToolbar createToolbar(@NotNull BsConsole console) {
    DefaultActionGroup group = new DefaultActionGroup();
    group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
    group.add(new ClearLogAction(console));
    group.add(new BsMakeAction());
    group.add(new BsMakeWorldAction());

    ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
    toolbar.setTargetComponent(console.getComponent());

    return toolbar;
  }
}
