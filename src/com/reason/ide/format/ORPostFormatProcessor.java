package com.reason.ide.format;

import com.intellij.lang.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.*;
import com.intellij.psi.impl.source.codeStyle.*;
import com.reason.comp.bs.*;
import com.reason.comp.dune.*;
import com.reason.comp.rescript.*;
import com.reason.ide.files.*;
import com.reason.ide.settings.*;
import com.reason.lang.core.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

/*
 java invocation:
      CodeStyleManager codeStyleManager = project.getService(CodeStyleManager.class);
      PsiElement reformat = codeStyleManager.reformat(file);
 */

/**
 * This class is called when user action 'reformat' (ctrl+alt+L) is fired, after standard format processor
 */
// It should be com.intellij.psi.codeStyle.ExternalFormatProcessor, but I can't make it work...
public class ORPostFormatProcessor implements PostFormatProcessor {
    private static final Log LOG = Log.create("format.postprocessor");

    @Override
    public @NotNull PsiElement processElement(@NotNull PsiElement source, @NotNull CodeStyleSettings settings) {
        FormatterProcessor formatter = source instanceof FileBase ? getFormatterProcessor((PsiFile) source) : null;
        if (formatter != null) {
            Language language = source.getLanguage();
            LOG.trace("Process element for language", language);

            String formattedText = formatter.apply(source.getText());
            return formattedText == null ? source : ORCodeFactory.createFileFromText(source.getProject(), language, formattedText);
        }
        return source;
    }

    @Override
    public @NotNull TextRange processText(@NotNull PsiFile source, @NotNull TextRange rangeToReformat, @NotNull CodeStyleSettings settings) {
        FormatterProcessor formatter = getFormatterProcessor(source);
        if (formatter != null) {
            Language language = source.getLanguage();
            PostFormatProcessorHelper postProcessorHelper = new PostFormatProcessorHelper(settings.getCommonSettings(language));
            LOG.trace("Process text for language", language);

            postProcessorHelper.setResultTextRange(rangeToReformat);
            int oldTextLength = source.getTextLength();

            Editor textEditor = FileEditorManager.getInstance(source.getProject()).getSelectedTextEditor();
            CaretModel caretModel = textEditor == null ? null : textEditor.getCaretModel();
            int caretOffset = caretModel == null ? 0 : caretModel.getOffset();

            String formattedText = formatter.apply(source.getText());
            if (formattedText != null) {
                PsiElement formattedElement = ORCodeFactory.createFileFromText(source.getProject(), language, formattedText);

                CodeEditUtil.removeChildren(source.getNode(), source.getNode().getFirstChildNode(), source.getNode().getLastChildNode());
                CodeEditUtil.addChildren(source.getNode(), formattedElement.getNode().getFirstChildNode(), formattedElement.getNode().getLastChildNode(), null);
                if (caretModel != null) {
                    caretModel.moveToOffset(caretOffset);
                }

                postProcessorHelper.updateResultRange(oldTextLength, source.getTextLength());
                return postProcessorHelper.getResultTextRange();
            }
        }
        return rangeToReformat;
    }

    public static @Nullable FormatterProcessor getFormatterProcessor(@NotNull PsiFile file) {
        FileType fileType = file.getFileType();
        if (FileHelper.isReason(fileType)) {
            return new RmlFormatProcessor(file);
        }
        if (FileHelper.isRescript(fileType)) {
            return new ResFormatProcessor(file);
        }
        if (FileHelper.isOCaml(fileType)) {
            return new OclFormatProcessor(file);
        }
        return null;
    }

    static class RmlFormatProcessor implements FormatterProcessor {
        private final Project m_project;
        private final VirtualFile m_file;
        private final boolean m_isInterface;

        RmlFormatProcessor(@NotNull PsiFile file) {
            m_project = file.getProject();
            m_file = file.getVirtualFile();
            m_isInterface = FileHelper.isInterface(file.getFileType());
        }

        @Override
        public @Nullable String apply(@NotNull String textToFormat) {
            if (m_project.getService(ORSettings.class).isBsEnabled() && m_file.exists()) {
                LOG.trace("Apply ReasonML formatter, is interface", m_isInterface);
                BsFormatProcess process = m_project.getService(BsFormatProcess.class);
                return process.convert(m_file, m_isInterface, "re", "re", textToFormat);
            }
            return null;
        }
    }

    static class ResFormatProcessor implements FormatterProcessor {
        private final Project m_project;
        private final VirtualFile m_file;
        private final boolean m_isInterface;

        ResFormatProcessor(@NotNull PsiFile file) {
            m_project = file.getProject();
            m_file = file.getVirtualFile();
            m_isInterface = FileHelper.isInterface(file.getFileType());
        }

        @Override
        public @Nullable String apply(@NotNull String textToFormat) {
            if (m_file.exists()) {
                LOG.trace("Apply Rescript formatter, is interface", m_isInterface);
                ResFormatProcess process = m_project.getService(ResFormatProcess.class);
                return process.format(m_file, m_isInterface, textToFormat);
            }
            return null;
        }
    }

    private static class OclFormatProcessor implements FormatterProcessor {
        private final Project m_project;
        private final VirtualFile m_file;

        public OclFormatProcessor(@NotNull PsiFile file) {
            m_project = file.getProject();
            m_file = file.getVirtualFile();
        }

        @Override
        public @Nullable String apply(@NotNull String textToFormat) {
            if (m_file.exists()) {
                LOG.trace("Apply OCaml formatter");
                OcamlFormatProcess process = m_project.getService(OcamlFormatProcess.class);
                return process == null ? null : process.format(m_file, textToFormat);
            }
            return null;
        }
    }
}
