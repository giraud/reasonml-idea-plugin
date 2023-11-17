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
import com.reason.*;
import com.reason.comp.bs.*;
import com.reason.comp.ocaml.*;
import com.reason.comp.rescript.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.settings.*;
import com.reason.lang.core.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

/*
 java invocation:
      CodeStyleManager manager = project.getService(CodeStyleManager.class);
      PsiElement reformat = manager.reformat(file);
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

                int newTextLength = source.getTextLength();
                if (newTextLength > oldTextLength) {
                    postProcessorHelper.updateResultRange(oldTextLength, newTextLength);
                }
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
        private final Project myProject;
        private final @Nullable VirtualFile myFile;
        private final boolean myIsInterface;

        RmlFormatProcessor(@NotNull PsiFile file) {
            myProject = file.getProject();
            myFile = ORFileUtils.getVirtualFile(file);
            myIsInterface = FileHelper.isInterface(file.getFileType());
        }

        @Override
        public @Nullable String apply(@NotNull String textToFormat) {
            if (myProject.getService(ORSettings.class).isBsEnabled() && myFile != null && myFile.exists()) {
                LOG.trace("Apply ReasonML formatter, is interface", myIsInterface);
                BsFormatProcess process = myProject.getService(BsFormatProcess.class);
                return process.convert(myFile, myIsInterface, "re", "re", textToFormat);
            }
            return null;
        }
    }

    static class ResFormatProcessor implements FormatterProcessor {
        private final Project myProject;
        private final @Nullable VirtualFile myFile;
        private final boolean myIsInterface;

        ResFormatProcessor(@NotNull PsiFile file) {
            myProject = file.getProject();
            myFile = ORFileUtils.getVirtualFile(file);
            myIsInterface = FileHelper.isInterface(file.getFileType());
        }

        @Override
        public @Nullable String apply(@NotNull String textToFormat) {
            if (myFile != null && myFile.exists()) {
                LOG.trace("Apply Rescript formatter, is interface", myIsInterface);
                ResFormatProcess process = myProject.getService(ResFormatProcess.class);
                return process.format(myFile, myIsInterface, textToFormat);
            }
            return null;
        }
    }

    private static class OclFormatProcessor implements FormatterProcessor {
        private final Project myProject;
        private final @Nullable VirtualFile myFile;

        public OclFormatProcessor(@NotNull PsiFile file) {
            myProject = file.getProject();
            myFile = ORFileUtils.getVirtualFile(file);
        }

        @Override
        public @Nullable String apply(@NotNull String textToFormat) {
            if (myFile != null && myFile.exists()) {
                LOG.trace("Apply OCaml formatter");
                OcamlFormatProcess process = myProject.getService(OcamlFormatProcess.class);
                return process == null ? null : process.format(myFile, textToFormat);
            }
            return null;
        }
    }
}
