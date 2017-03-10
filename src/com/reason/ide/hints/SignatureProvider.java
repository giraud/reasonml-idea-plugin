package com.reason.ide.hints;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.codeInsight.hints.MethodInfo;
import com.intellij.codeInsight.hints.Option;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.psi.ReasonMLLetStatement;
import com.reason.psi.ReasonMLParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

// just an experiment
public class SignatureProvider implements InlayParameterHintsProvider {

    @NotNull
    @Override
    public List<InlayInfo> getParameterHints(PsiElement element) {
        if (element instanceof ReasonMLLetStatement) {
            Collection<ReasonMLParameter> childrenOfType = PsiTreeUtil.findChildrenOfType(element, ReasonMLParameter.class);

            List<InlayInfo> result = new ArrayList<>();
            for (ReasonMLParameter reasonMLParameter : childrenOfType) {
                result.add(new InlayInfo("int", reasonMLParameter.getTextOffset()));
            }
            return result;
        }

        return emptyList();
    }

    @Nullable
    @Override
    public MethodInfo getMethodInfo(PsiElement element) {
        System.out.println("getMethodInfo " + element);
        if (element instanceof ReasonMLLetStatement) {
            MethodInfo methodInfo = new MethodInfo("fqn", asList("p1", "p2"));
            return methodInfo;
        }
        return null;
    }

    @NotNull
    @Override
    public Set<String> getDefaultBlackList() {
        return emptySet();
    }

    @Nullable
    @Override
    public Language getBlackListDependencyLanguage() {
        return null;
    }

    @NotNull
    @Override
    public List<Option> getSupportedOptions() {
        return emptyList();
    }
}
