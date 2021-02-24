package com.reason.lang.core.psi.impl;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.psi.search.GlobalSearchScope.*;
import static com.reason.lang.core.ExpressionFilterConstants.*;
import static com.reason.lang.core.ORFileType.*;
import static com.reason.lang.core.psi.ExpressionScope.*;

public class PsiTagStartImpl extends CompositeTypePsiElement<ORTypes> implements PsiTagStart {

    protected PsiTagStartImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    static class TagPropertyImpl implements TagProperty {
        private final @Nullable String m_name;
        private final String m_type;
        private final PsiElement m_psiElement;
        private boolean m_mandatory;

        TagPropertyImpl(@NotNull PsiRecordField field, @NotNull List<PsiAnnotation> annotations) {
            m_psiElement = field;
            m_name = field.getName();
            PsiSignature signature = field.getSignature();
            m_type = signature == null ? "" : signature.asText(RmlLanguage.INSTANCE);
            m_mandatory = false; // TODO: hmSignature.isMandatory(0);

            for (PsiAnnotation annotation : annotations) {
                if ("@bs.optional".equals(annotation.getName())) {
                    m_mandatory = false;
                }
            }
        }

        TagPropertyImpl(@NotNull PsiParameter parameter) {
            m_psiElement = parameter;
            m_name = parameter.getName();
            PsiSignature signature = parameter.getSignature();
            m_type = signature == null ? "" : signature.asText(parameter.getLanguage());
            m_mandatory = parameter.getDefaultValue() != null;
        }

        public TagPropertyImpl(@NotNull PsiSignatureItem signatureItem) {
            m_psiElement = signatureItem;
            m_name = signatureItem.getName();
            m_type = signatureItem.asText(signatureItem.getLanguage());
            m_mandatory = !signatureItem.isOptional();
        }

        TagPropertyImpl(@Nullable String name, String type) {
            m_psiElement = null;
            m_name = name;
            m_type = type;
            m_mandatory = false;
        }

        @Override public @Nullable PsiElement getElement() {
            return m_psiElement;
        }

        @Override
        public @Nullable String getName() {
            return m_name;
        }

        @Override
        public String getType() {
            return m_type;
        }

        @Override
        public boolean isMandatory() {
            return m_mandatory;
        }

        @Override
        public @NotNull String toString() {
            return m_name + ":" + m_type;
        }
    }

    @NotNull
    public static TagProperty createProp(String name, String type) {
        return new TagPropertyImpl(name, type);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        PsiElement lastTag = null;

        Collection<PsiLeafTagName> tags = PsiTreeUtil.findChildrenOfType(this, PsiLeafTagName.class);
        if (!tags.isEmpty()) {
            for (PsiLeafTagName tag : tags) {
                PsiElement currentStart = tag.getParent().getParent();
                if (currentStart == this) {
                    lastTag = tag.getParent();
                }
            }
        }

        return lastTag;
    }

    @Nullable
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @NotNull
    @Override
    public List<PsiTagProperty> getProperties() {
        return ORUtil.findImmediateChildrenOfClass(this, PsiTagProperty.class);
    }

    @NotNull
    @Override
    public List<TagProperty> getUnifiedPropertyList() {
        final List<TagProperty> result = new ArrayList<>();

        Project project = getProject();
        PsiFinder psiFinder = PsiFinder.getInstance(project);
        GlobalSearchScope scope = allScope(project);

        // find tag 'make' expression
        PsiElement tagName = getNameIdentifier();
        if (tagName == null) {
            // no tag name, it's not a custom tag
            tagName = ORUtil.findImmediateFirstChildOfClass(this, PsiLowerSymbol.class);
            if (tagName != null) {
                Set<PsiModule> modules =
                        psiFinder.findModulesbyName("ReactDOMRe", interfaceOrImplementation, module -> module instanceof FileBase, scope);
                PsiModule reactDOMRe = modules.isEmpty() ? null : modules.iterator().next();
                if (reactDOMRe != null) {
                    PsiType props = reactDOMRe.getTypeExpression("props");
                    if (props != null) {
                        PsiTypeBinding binding = PsiTreeUtil.getStubChildOfType(props, PsiTypeBinding.class);
                        if (binding != null) {
                            PsiRecord record = PsiTreeUtil.getStubChildOfType(binding, PsiRecord.class);
                            if (record != null) {
                                for (PsiRecordField field : record.getFields()) {
                                    result.add(new TagPropertyImpl(field, ORUtil.prevAnnotations(field)));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // The tag is a custom component
            QNameFinder qNameFinder = PsiFinder.getQNameFinder(getLanguage());
            Set<String> paths = qNameFinder.extractPotentialPaths(tagName);
            for (String path : paths) {
                PsiModule module = psiFinder.findComponentFromQName(path, scope);
                if (module != null) {
                    Collection<PsiNamedElement> expressions = module.getExpressions(pub, FILTER_LET_OR_EXTERNAL);
                    for (PsiNamedElement expression : expressions) {
                        if ("make".equals(expression.getName())) {
                            if (expression instanceof PsiLet) {
                                PsiFunction function = ((PsiLet) expression).getFunction();
                                if (function != null) {
                                    function.getParameters().stream()
                                            .filter(p -> !"children".equals(p.getName()) && !"_children".equals(p.getName()))
                                            .forEach(p -> result.add(new TagPropertyImpl(p)));
                                }
                            } else {
                                PsiSignature signature = ((PsiExternal) expression).getSignature();
                                if (signature != null) {
                                    signature.getItems().stream()
                                            .filter(p -> !"children".equals(p.getName()) && !"_children".equals(p.getName()))
                                            .forEach(p -> result.add(new TagPropertyImpl(p)));
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "Tag start";
    }
}
