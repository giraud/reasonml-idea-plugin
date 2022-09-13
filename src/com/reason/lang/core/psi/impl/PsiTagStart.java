package com.reason.lang.core.psi.impl;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiTagStart extends ORCompositePsiElement<ORTypes> implements PsiNameIdentifierOwner {
    protected PsiTagStart(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public static @NotNull ComponentPropertyAdapter createProp(String name, String type) {
        return new ComponentPropertyAdapter(name, type);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        PsiElement lastTag = null;

        PsiElement element = getFirstChild();
        IElementType elementType = element == null ? null : element.getNode().getElementType();
        while (elementType == myTypes.A_UPPER_TAG_NAME || elementType == myTypes.A_LOWER_TAG_NAME || elementType == myTypes.DOT || elementType == myTypes.LT) {
            if (elementType != myTypes.DOT && elementType != myTypes.LT) {
                lastTag = element;
            }
            element = element.getNextSibling();
            elementType = element == null ? null : element.getNode().getElementType();
        }

        return lastTag;
    }

    @Override
    public @Nullable String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    public @NotNull List<PsiTagProperty> getProperties() {
        return ORUtil.findImmediateChildrenOfClass(this, PsiTagProperty.class);
    }

    public @NotNull List<ComponentPropertyAdapter> getUnifiedPropertyList() {
        final List<ComponentPropertyAdapter> result = new ArrayList<>();

        Project project = getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        // find tag 'make' expression
        PsiElement tagName = getNameIdentifier();
        if (tagName instanceof PsiUpperSymbol) {
            PsiUpperSymbolReference reference = (PsiUpperSymbolReference) tagName.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            if (resolvedElement instanceof PsiLet) {
                PsiFunction makeFunction = ((PsiLet) resolvedElement).getFunction();
                if (makeFunction != null) {
                    makeFunction.getParameters().stream()
                            .filter(p -> !"children".equals(p.getName()) && !"_children".equals(p.getName()))
                            .forEach(p -> result.add(new ComponentPropertyAdapter(p)));
                }
            } else if (resolvedElement instanceof PsiExternal) {
                PsiSignature signature = ((PsiExternal) resolvedElement).getSignature();
                if (signature != null) {
                    signature.getItems().stream()
                            .filter(p -> !"children".equals(p.getName()) && !"_children".equals(p.getName()))
                            .forEach(p -> result.add(new ComponentPropertyAdapter(p)));
                }
            }
        } else if (tagName == null) {
            // no tag name, it's not a custom tag
            tagName = ORUtil.findImmediateFirstChildOfClass(this, PsiLowerSymbol.class);
            if (tagName != null) {
                Collection<PsiType> reactDomPropsType = TypeFqnIndex.getElements("ReactDom.props".hashCode(), project, scope);
                if (reactDomPropsType.isEmpty()) {
                    // Old bindings
                    reactDomPropsType = TypeFqnIndex.getElements("ReactDomRe.props".hashCode(), project, scope);
                }

                PsiType props = reactDomPropsType.isEmpty() ? null : reactDomPropsType.iterator().next();
                if (props != null) {
                    PsiTypeBinding binding = PsiTreeUtil.getStubChildOfType(props, PsiTypeBinding.class);
                    if (binding != null) {
                        PsiRecord record = PsiTreeUtil.getStubChildOfType(binding, PsiRecord.class);
                        if (record != null) {
                            for (PsiRecordField field : record.getFields()) {
                                result.add(new ComponentPropertyAdapter(field, ORUtil.prevAnnotations(field)));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "PsiTagStart";
    }
}
