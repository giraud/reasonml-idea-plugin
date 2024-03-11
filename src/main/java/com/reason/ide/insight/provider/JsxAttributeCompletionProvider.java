package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.ui.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.*;

public class JsxAttributeCompletionProvider {
    private static final Log LOG = Log.create("insight.jsx.attribute");

    private JsxAttributeCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull GlobalSearchScope scope, @NotNull CompletionResultSet resultSet) {
        LOG.debug("JSX attribute completion");

        RPsiTagStart tag = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiTagStart.class);
        ORLanguageProperties langProperties = ORLanguageProperties.cast(tag == null ? null : tag.getLanguage());
        Project project = element.getProject();

        // Attributes already used
        List<RPsiTagProperty> usedAttributes = tag == null ? Collections.emptyList() : tag.getProperties();
        List<String> usedNames = usedAttributes.stream().map(RPsiTagProperty::getName).collect(toList());
        LOG.debug("used names", usedNames);

        PsiElement tagName = tag == null ? null : tag.getNameIdentifier();
        if (tagName instanceof RPsiUpperSymbol) {
            // Custom component
            ORPsiUpperSymbolReference tagReference = (ORPsiUpperSymbolReference) tagName.getReference();
            PsiElement resolvedModule = tagReference == null ? null : tagReference.resolve();
            PsiElement resolvedElement = resolvedModule instanceof RPsiModule ? ((RPsiModule) resolvedModule).getMakeFunction() : null;

            // Additional attributes for UpperSymbol => only key and ref
            if (resolvedElement != null) {
                LOG.debug("Tag found", resolvedElement);
                if (!usedNames.contains("key")) {
                    addProperty(null, "key", "string=?", false, resultSet);
                }
                if (!usedNames.contains("ref")) {
                    addProperty(null, "ref", "domRef=?", false, resultSet);
                }
            }

            Predicate<PsiNamedElement> propertyFilter = i -> {
                String name = i.getName();
                return !"children".equals(name) && !"_children".equals(name) && !usedNames.contains(name);
            };

            if (resolvedElement instanceof RPsiLet) {
                RPsiFunction makeFunction = ((RPsiLet) resolvedElement).getFunction();
                if (makeFunction != null) {
                    makeFunction.getParameters().stream()
                            .filter(propertyFilter)
                            .forEach(i -> addProperty(i, i.getName(), getParameterSignature(i, langProperties), i.getDefaultValue() == null, resultSet));
                }
            } else if (resolvedElement instanceof RPsiExternal) {
                RPsiSignature signature = ((RPsiExternal) resolvedElement).getSignature();
                if (signature != null) {
                    signature.getItems().stream()
                            .filter(propertyFilter)
                            .forEach(i -> addProperty(i, i.getName(), i.asText(langProperties), !i.isOptional(), resultSet));
                }
            }
        } else if (tagName instanceof LeafPsiElement) {
            LOG.debug("Completion of a standard tag");

            Collection<RPsiType> propsType;

            // Try to locate the definition of the react props
            // -----------------------------------------------

            BsConfig config = project.getService(ORCompilerConfigManager.class).getNearestConfig(element.getContainingFile());
            String jsxVersion = config != null && config.getJsxVersion() != null ? config.getJsxVersion() : "3";
            if (jsxVersion.equals("4")) {
                String domPropsQName = "JsxDOM" + (config.isUncurried() ? "U" : "C") + ".domProps";
                LOG.debug("New Jsx props, using", domPropsQName);
                propsType = TypeFqnIndex.getElements(domPropsQName, project, scope);
            } else {
                propsType = TypeFqnIndex.getElements("ReactDOM.Props.domProps", project, scope);
                if (propsType.isEmpty()) {
                    // Old bindings
                    propsType = TypeFqnIndex.getElements("ReactDom.props", project, scope);
                    if (propsType.isEmpty()) {
                        propsType = TypeFqnIndex.getElements("ReactDomRe.props", project, scope);
                    }
                }
            }

            // Add props to the list of completions
            // ------------------------------------

            RPsiType props = propsType.isEmpty() ? null : propsType.iterator().next();
            if (props != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Properties found", props.getRecordFields().size());
                }

                ORLangTypes types = ORUtil.getTypes(tag.getLanguage());

                for (RPsiRecordField field : props.getRecordFields()) {
                    String fieldName = field.getName();
                    if (usedNames.contains(fieldName) || "children".equals(fieldName)) {
                        continue;
                    }

                    RPsiSignature signature = field.getSignature();
                    List<RPsiSignatureItem> items = signature != null ? signature.getItems() : null;
                    RPsiSignatureItem firstSigItem = items == null || items.isEmpty() ? null : items.get(0);

                    PsiElement nextSibling = ORUtil.nextSibling(field.getNameIdentifier());
                    boolean isOptionalKey = nextSibling != null && nextSibling.getNode().getElementType() == types.QUESTION_MARK;
                    boolean isMandatory = !isOptionalKey && firstSigItem != null && !firstSigItem.isOptional();
                    for (RPsiAnnotation annotation : ORUtil.prevAnnotations(field)) {
                        if ("@bs.optional".equals(annotation.getName())) {
                            isMandatory = false;
                            break;
                        }
                    }
                    addProperty(field, field.getName(), signature != null ? signature.asText(langProperties) : "", isMandatory, resultSet);
                }
            }
        }
    }

    private static @NotNull String getParameterSignature(RPsiParameterDeclaration param, @Nullable ORLanguageProperties languageProperties) {
        RPsiSignature signature = param.getSignature();
        if (signature == null) {
            return (param.getDefaultValue() != null ? "=" + param.getDefaultValue().getText() : "");
        }

        return signature.asText(languageProperties);
    }

    private static void addProperty(@Nullable PsiElement element, @Nullable String name, @Nullable String type, boolean isMandatory, CompletionResultSet resultSet) {
        if (name != null) {
            Icon icon = isMandatory ? LayeredIcon.create(ORIcons.ATTRIBUTE, ORIcons.OVERLAY_MANDATORY) : ORIcons.ATTRIBUTE;
            LookupElementBuilder lookupElementBuilder = element == null ? LookupElementBuilder.create(name) : LookupElementBuilder.createWithSmartPointer(name, element);
            resultSet.addElement(PrioritizedLookupElement.withPriority(
                    lookupElementBuilder
                            .withBoldness(isMandatory)
                            .withTypeText(type, true)
                            .withIcon(icon)
                            .withInsertHandler((context, item) -> insertTagAttributeHandler(context)),
                    isMandatory ? 1 : 0));
        }
    }

    private static void insertTagAttributeHandler(@NotNull InsertionContext context) {
        context.setAddCompletionChar(false);

        Editor editor = context.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, "={}");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
    }
}
