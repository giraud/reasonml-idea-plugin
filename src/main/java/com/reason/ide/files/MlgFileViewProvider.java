package com.reason.ide.files;

import com.intellij.lang.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.*;
import com.intellij.psi.templateLanguages.*;
import com.intellij.psi.tree.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.ocamlgrammar.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class MlgFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider {
    public static final IElementType OUTER_ELEMENT = new OuterLanguageElementType("OUTER_ELEMENT", OclGrammarLanguage.INSTANCE);
    private static final IElementType TEMPLATE_DATA = new TemplateDataElementType("TEMPLATE_DATA", OclGrammarLanguage.INSTANCE, OclGrammarTypes.INSTANCE.TEMPLATE_OCAML_TEXT, OUTER_ELEMENT);

    public MlgFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file, boolean eventSystemEnabled) {
        super(manager, file, eventSystemEnabled);
    }

    @Override
    public @NotNull Language getBaseLanguage() {
        return OclGrammarLanguage.INSTANCE;
    }

    @Override
    public @NotNull Language getTemplateDataLanguage() {
        return OclLanguage.INSTANCE;
    }

    @Override
    public @NotNull Set<Language> getLanguages() {
        return Set.of(OclGrammarLanguage.INSTANCE, getTemplateDataLanguage());
    }

    @Override
    protected @NotNull MultiplePsiFilesPerDocumentFileViewProvider cloneInner(@NotNull VirtualFile fileCopy) {
        return new MlgFileViewProvider(getManager(), fileCopy, false);
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
