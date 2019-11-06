package com.reason.ide.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlLiveTemplateProvider implements DefaultLiveTemplatesProvider {

    private static final String[] TEMPLATE_FILES = {"liveTemplates/Reason"};

    @NotNull
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return TEMPLATE_FILES;
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return ArrayUtil.EMPTY_STRING_ARRAY;
    }
}
