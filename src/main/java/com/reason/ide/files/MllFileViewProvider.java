package com.reason.ide.files;

import com.intellij.lang.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.*;
import com.intellij.psi.templateLanguages.*;
import com.intellij.psi.tree.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.ocamllex.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class MllFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider {
    public static IElementType OUTER_ELEMENT = new OuterLanguageElementType("OUTER_ELEMENT", OclLexLanguage.INSTANCE);

    private static final IElementType TEMPLATE_DATA = new TemplateDataElementType("TEMPLATE_DATA", OclLexLanguage.INSTANCE, OclLexTypes.INSTANCE.TEMPLATE_OCAML_TEXT, OUTER_ELEMENT);

    public MllFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file, boolean eventSystemEnabled) {
        super(manager, file, eventSystemEnabled);
    }

    @Override
    public @NotNull Language getBaseLanguage() {
        return OclLexLanguage.INSTANCE;
    }

    @Override
    public @NotNull Language getTemplateDataLanguage() {
        return OclLanguage.INSTANCE;
    }

    @Override
    public @NotNull Set<Language> getLanguages() {
        return Set.of(OclLexLanguage.INSTANCE, getTemplateDataLanguage());
    }

    @Override
    protected @NotNull MultiplePsiFilesPerDocumentFileViewProvider cloneInner(@NotNull VirtualFile fileCopy) {
        return new MllFileViewProvider(getManager(), fileCopy, false);
    }

    @Nullable
    protected PsiFile createFile(@NotNull Language lang) {
        ParserDefinition parser = LanguageParserDefinitions.INSTANCE.forLanguage(lang);
        if (parser == null) {
            return null;
        }

        if (lang == OclLanguage.INSTANCE) {
            PsiFileImpl file = (PsiFileImpl) parser.createFile(this);
            file.setContentElementType(TEMPLATE_DATA);
            return file;
        }

        return lang == getBaseLanguage() ? parser.createFile(this) : null;
    }
}
