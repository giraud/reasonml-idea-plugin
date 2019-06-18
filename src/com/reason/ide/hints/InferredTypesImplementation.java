package com.reason.ide.hints;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.Stack;
import com.reason.Joiner;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class InferredTypesImplementation implements InferredTypes {

    private static final String OPEN = "Op";
    private static final String MODULE_GHOST = "Mg";
    private static final String VALUE = "Va";
    private static final String IDENT = "Id";

    private final Map<String, Stack<OpenModule>> m_opens = new THashMap<>();
    private final Map<Integer, LogicalORSignature> m_vals = new THashMap<>();

    private final Map<LogicalPosition, ORSignature> m_signatures = new THashMap<>();

    @NotNull
    public Map<Integer, String> signaturesByLines(@NotNull Language lang) {
        Map<Integer, String> result = new THashMap<>();

        for (Stack<OpenModule> openStack : m_opens.values()) {
            for (OpenModule openModule : openStack) {
                String exposing = openModule.getExposing();
                if (exposing != null) {
                    result.put(openModule.getLine(), exposing);
                }
            }
        }

        for (Map.Entry<Integer, LogicalORSignature> entry : m_vals.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getSignature().asString(lang));
        }

        return result;
    }

    @Override
    public ORSignature getSignatureByPosition(@NotNull LogicalPosition elementPosition) {
        return m_signatures.get(elementPosition);
    }

    public void add(@NotNull Project project, @NotNull String entry, @NotNull LogicalPosition start, @NotNull String line) {
        if (OPEN.equals(entry)) {
            // Pattern :: Name
            Stack<OpenModule> openStack = m_opens.get(line);
            if (openStack == null) {
                openStack = new Stack<>();
                m_opens.put(line, openStack);
            }

            openStack.push(new OpenModule(start));
        } else if (VALUE.equals(entry)) {
            // Pattern :: Name|type
            String[] tokens = line.split("\\|", 2);

            PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + tokens[1]);
            PsiSignature parsedSignature = PsiTreeUtil.findChildOfType(psiFile, PsiSignature.class);
            if (parsedSignature != null) {
                ORSignature orSignature = parsedSignature.asHMSignature();
                addVisibleSignature(start, orSignature);
                m_signatures.put(start, orSignature);
            }
        } else if (MODULE_GHOST.equals(entry)) {
            // Pattern :: name|type
            String[] tokens = line.split("\\|", 2);
            String signature = tokens[1].startsWith("type t = ") ? tokens[1].substring(9) : tokens[1];
            PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + signature);
            PsiSignature parsedSignature = PsiTreeUtil.findChildOfType(psiFile, PsiSignature.class);
            if (parsedSignature != null) {
                ORSignature orSignature = parsedSignature.asHMSignature();
                addVisibleSignature(start, orSignature);
                m_signatures.put(start, orSignature);
            }        } else if (IDENT.equals(entry)) {
            // Pattern :: name|qName|type
            String[] tokens = line.split("\\|", 3);

            PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + tokens[2]);
            PsiSignature parsedSignature = PsiTreeUtil.findChildOfType(psiFile, PsiSignature.class);
            if (parsedSignature != null) {
                ORSignature orSignature = parsedSignature.asHMSignature();
                m_signatures.put(start, orSignature);
            }

            if (!tokens[0].equals(tokens[1])) {
                int lastDot = tokens[1].lastIndexOf(".");
                if (0 < lastDot) {
                    String path = tokens[1].substring(0, lastDot);
                    Stack<OpenModule> openStack = m_opens.get(path);
                    if (openStack != null) {
                        openStack.peek().addId(tokens[0]);
                    }
                }
            }
        }
    }

    private void addVisibleSignature(@NotNull LogicalPosition pos, @NotNull ORSignature signature) {
        LogicalORSignature savedSignature = m_vals.get(pos.line);
        if (savedSignature == null || pos.column < savedSignature.getLogicalPosition().column) {
            m_vals.put(pos.line, new LogicalORSignature(pos, signature));
        }
    }

    static class OpenModule {
        @NotNull
        private final LogicalPosition m_position;
        private final Set<String> m_values = new THashSet<>();

        OpenModule(LogicalPosition start) {
            m_position = start;
        }

        Integer getLine() {
            return m_position.line;
        }

        @Nullable
        String getExposing() {
            return m_values.isEmpty() ? null : "exposing: " + Joiner.join(", ", m_values);
        }

        void addId(String id) {
            m_values.add(id);
        }
    }

    static class LogicalORSignature {
        @NotNull
        private final LogicalPosition m_logicalPosition;
        @NotNull
        private final ORSignature m_signature;

        LogicalORSignature(@NotNull LogicalPosition position, @NotNull ORSignature signature) {
            m_logicalPosition = position;
            m_signature = signature;
        }

        @NotNull
        LogicalPosition getLogicalPosition() {
            return m_logicalPosition;
        }

        @NotNull
        public ORSignature getSignature() {
            return m_signature;
        }
    }

}
