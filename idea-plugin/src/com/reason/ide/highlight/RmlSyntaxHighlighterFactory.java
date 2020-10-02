package com.reason.ide.highlight;

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

public class RmlSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  @NotNull
  @Override
  public com.intellij.openapi.fileTypes.SyntaxHighlighter getSyntaxHighlighter(
      Project project, VirtualFile virtualFile) {
    return new ORSyntaxHighlighter(RmlTypes.INSTANCE);
  }
}
