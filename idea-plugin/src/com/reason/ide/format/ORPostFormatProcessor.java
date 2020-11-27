package com.reason.ide.format;

import com.intellij.openapi.components.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.*;
import com.intellij.psi.impl.source.codeStyle.*;
import com.reason.bs.*;
import com.reason.dune.*;
import com.reason.ide.files.*;
import com.reason.ide.settings.*;
import com.reason.lang.core.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

/**
 * This class is called when user action 'reformat' (ctrl+alt+L) is fired, after standard format processor
 */
// It should be com.intellij.psi.codeStyle.ExternalFormatProcessor, but I can't make it work...
public class ORPostFormatProcessor implements PostFormatProcessor {
  @Override
  public @NotNull PsiElement processElement(@NotNull PsiElement source, @NotNull CodeStyleSettings settings) {
    if (source instanceof PsiFile) {
      // Only the whole file can be formatted for now...
      CodeStyleManager codeStyleManager = ServiceManager.getService(source.getProject(), CodeStyleManager.class);
      return codeStyleManager.reformat(source);
    }
    return source;
  }

  @Override
  public @NotNull TextRange processText(@NotNull PsiFile source, @NotNull TextRange rangeToReformat, @NotNull CodeStyleSettings settings) {
    FormatterProcessor formatter = getFormatterProcessor(source);
    if (formatter != null) {
      PostFormatProcessorHelper postProcessorHelper = new PostFormatProcessorHelper(settings.getCommonSettings(source.getLanguage()));

      postProcessorHelper.setResultTextRange(rangeToReformat);
      int oldTextLength = source.getTextLength();

      PsiElement formattedElement = formatter.apply(source.getText());
      if (formattedElement != null) {
        // update ?
        CodeEditUtil.removeChildren(source.getNode(), source.getNode().getFirstChildNode(), source.getNode().getLastChildNode());
        CodeEditUtil.addChildren(source.getNode(), formattedElement.getNode().getFirstChildNode(), formattedElement.getNode().getLastChildNode(), null);

        postProcessorHelper.updateResultRange(oldTextLength, source.getTextLength());
        return postProcessorHelper.getResultTextRange();
      }
    }
    return rangeToReformat;
  }

  @Nullable
  private FormatterProcessor getFormatterProcessor(PsiFile file) {
    FileType fileType = file.getFileType();
    if (FileHelper.isReason(fileType)) {
      return new RefmtProcessor(file);
    }
    if (FileHelper.isRescript(fileType)) {
      return new RefmtProcessor(file); // TODO: RescriptProcessor
    }
    if (FileHelper.isOCaml(fileType)) {
      return new OcamlFormatProcessor(file);
    }
    return null;
  }

  static class RefmtProcessor implements FormatterProcessor {
    private final Project m_project;
    private final VirtualFile m_file;
    private final boolean m_isInterface;

    RefmtProcessor(@NotNull PsiFile file) {
      m_project = file.getProject();
      m_file = file.getVirtualFile();
      m_isInterface = FileHelper.isInterface(file.getFileType());
    }

    @Override
    public @Nullable PsiElement apply(@NotNull String textToFormat) {
      if (ORSettings.getInstance(m_project).isBsEnabled() && m_file.exists()) {
        String format = m_isInterface ? "rei" : "re"; // TODO ReformatUtil
        RefmtProcess process = RefmtProcess.getInstance(m_project);
        String newText = process.run(m_file, m_isInterface, format, textToFormat);
        return ORCodeFactory.createFileFromText(m_project, RmlLanguage.INSTANCE, newText);
      }
      return null;
    }
  }

  private static class OcamlFormatProcessor implements FormatterProcessor {
    private final Project m_project;
    private final VirtualFile m_file;
    private final boolean m_isInterface; // TODO

    public OcamlFormatProcessor(@NotNull PsiFile file) {
      m_project = file.getProject();
      m_file = file.getVirtualFile();
      m_isInterface = FileHelper.isInterface(file.getFileType());
    }

    @Override
    public @Nullable PsiElement apply(@NotNull String textToFormat) {
      if (m_file.exists()) {
        OcamlFormatProcess process = OcamlFormatProcess.getInstance(m_project);
        String newText = process.format(m_file, textToFormat);
        return ORCodeFactory.createFileFromText(m_project, OclLanguage.INSTANCE, newText);
      }
      return null;
    }
  }
}
