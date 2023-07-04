package com.reason.ide.hints;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.containers.Stack;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.ocaml.*;
import gnu.trove.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class InferredTypesImplementation implements InferredTypes {
    private static final String OPEN = "Op";
    private static final String MODULE_GHOST = "Mg";
    private static final String VALUE = "Va";
    private static final String IDENT = "Id";
    private static final String PARAM = "Pa";
    private static final String RECORD_FIELD = "Rf";

    private final Map<String, Stack<OpenModule>> myOpens = new THashMap<>();
    private final Map<Integer, LogicalORSignature> myVals = new THashMap<>();
    private final Map<LogicalPosition, RPsiSignature> mySignatures = new THashMap<>();

    public @NotNull Map<Integer, LogicalPositionSignature> signaturesByLines(@Nullable ORLanguageProperties lang) {
        Map<Integer, LogicalPositionSignature> result = new THashMap<>();

        for (Stack<OpenModule> openStack : myOpens.values()) {
            for (OpenModule openModule : openStack) {
                String exposing = openModule.getExposing();
                if (exposing != null) {
                    result.put(openModule.getLine(), makeLogicalPositionSignature(0, 0, exposing));
                }
            }
        }

        for (Map.Entry<Integer, LogicalORSignature> entry : myVals.entrySet()) {
            result.put(entry.getKey(), makeLogicalPositionSignature(lang, entry.getValue()));
        }

        return result;
    }

    private @NotNull LogicalPositionSignature makeLogicalPositionSignature(@Nullable ORLanguageProperties lang, @NotNull LogicalORSignature value) {
        return makeLogicalPositionSignature(value.getLogicalStart().column, value.getLogicalEnd().column, value.getSignature().asText(lang));
    }

    private @NotNull LogicalPositionSignature makeLogicalPositionSignature(int colStart, int colEnd, @NotNull String signature) {
        LogicalPositionSignature result = new LogicalPositionSignature();
        result.colStart = colStart;
        result.colEnd = colEnd;
        result.signature = signature;
        return result;
    }

    @Override
    public @Nullable RPsiSignature getSignatureByPosition(@NotNull LogicalPosition elementPosition) {
        return mySignatures.get(elementPosition);
    }

    public void add(@NotNull Project project, @NotNull String entry, @NotNull LogicalPosition start, @NotNull LogicalPosition end, @NotNull String line) {
        switch (entry) {
            case OPEN:
                // Pattern :: Name
                Stack<OpenModule> openStack = myOpens.get(line);
                if (openStack == null) {
                    openStack = new Stack<>();
                    myOpens.put(line, openStack);
                }

                openStack.push(new OpenModule(start));
                break;
            case VALUE: {
                // Pattern :: Name|type
                String[] tokens = line.split("\\|", 2);
                PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + tokens[1]);
                RPsiSignature psiSignature = PsiTreeUtil.findChildOfType(psiFile, RPsiSignature.class);
                if (psiSignature != null) {
                    addVisibleSignature(start, end, psiSignature);
                    mySignatures.put(start, psiSignature);
                }
                break;
            }
            case MODULE_GHOST: {
                // Pattern :: name|type
                String[] tokens = line.split("\\|", 2);
                String signature = tokens[1].startsWith("type t = ") ? tokens[1].substring(9) : tokens[1];
                PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + signature);
                RPsiSignature psiSignature = PsiTreeUtil.findChildOfType(psiFile, RPsiSignature.class);
                if (psiSignature != null) {
                    addVisibleSignature(start, end, psiSignature);
                    mySignatures.put(start, psiSignature);
                }
                break;
            }
            case IDENT: {
                // Pattern :: name|qName|type
                String[] tokens = line.split("\\|", 3);

                PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + tokens[2]);
                RPsiSignature psiSignature = PsiTreeUtil.findChildOfType(psiFile, RPsiSignature.class);
                if (psiSignature != null) {
                    mySignatures.put(start, psiSignature);
                }

                if (!tokens[0].equals(tokens[1])) {
                    int lastDot = tokens[1].lastIndexOf(".");
                    if (0 < lastDot) {
                        String path = tokens[1].substring(0, lastDot);
                        Stack<OpenModule> open = myOpens.get(path);
                        if (open != null) {
                            open.peek().addId(tokens[0]);
                        }
                    }
                }
                break;
            }
            case PARAM: {
                // Pattern :: name|type
                String[] tokens = line.split("\\|", 2);

                PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + tokens[1]);
                RPsiSignature psiSignature = PsiTreeUtil.findChildOfType(psiFile, RPsiSignature.class);
                if (psiSignature != null) {
                    mySignatures.put(start, psiSignature);
                }
            }
            case RECORD_FIELD: {
                // Pattern :: name|type
                String[] tokens = line.split("\\|", 2);

                PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Dummy", OclLanguage.INSTANCE, "let x:" + tokens[1]);
                RPsiSignature psiSignature = PsiTreeUtil.findChildOfType(psiFile, RPsiSignature.class);
                if (psiSignature != null) {
                    mySignatures.put(start, psiSignature);
                }
            }
        }
    }

    private void addVisibleSignature(@NotNull LogicalPosition lStart, @NotNull LogicalPosition lEnd, @NotNull RPsiSignature signature) {
        LogicalORSignature savedSignature = myVals.get(lStart.line);
        if (savedSignature == null || lStart.column < savedSignature.getLogicalStart().column) {
            myVals.put(lStart.line, new LogicalORSignature(lStart, lEnd, signature));
        }
    }

    static class OpenModule {
        @NotNull
        private final LogicalPosition m_position;
        private final Set<String> m_values = new THashSet<>();

        OpenModule(@NotNull LogicalPosition start) {
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
        private final LogicalPosition m_lStart;
        @NotNull
        private final LogicalPosition m_lEnd;
        @NotNull
        private final RPsiSignature m_signature;

        LogicalORSignature(@NotNull LogicalPosition lStart, @NotNull LogicalPosition lEnd, @NotNull RPsiSignature signature) {
            m_lStart = lStart;
            m_lEnd = lEnd;
            m_signature = signature;
        }

        @NotNull
        LogicalPosition getLogicalStart() {
            return m_lStart;
        }

        @NotNull
        LogicalPosition getLogicalEnd() {
            return m_lEnd;
        }

        @NotNull
        public RPsiSignature getSignature() {
            return m_signature;
        }
    }
}
