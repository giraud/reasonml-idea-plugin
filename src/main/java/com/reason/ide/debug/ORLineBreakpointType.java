package com.reason.ide.debug;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.reason.FileHelper;
import com.reason.lang.core.type.ORLangTypes;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORLineBreakpointType extends XLineBreakpointType<ORLineBreakpointProperties> {
    private static final String ID = "OCamlLineBreakpoint";
    private static final String NAME = "Line breakpoint";

    protected ORLineBreakpointType() {
        super(ID, NAME);
    }

    @Nullable
    @Override
    public ORLineBreakpointProperties createBreakpointProperties(
            @NotNull VirtualFile file, int line) {
        return new ORLineBreakpointProperties();
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        FileType fileType = file.getFileType();
        if (FileHelper.isOCaml(fileType) || FileHelper.isReason(fileType)) {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                LineBreakpointAvailabilityProcessor canPutAtChecker =
                        new LineBreakpointAvailabilityProcessor(
                                FileHelper.isOCaml(fileType) ? OclTypes.INSTANCE : RmlTypes.INSTANCE);
                XDebuggerUtil.getInstance().iterateLine(project, document, line, canPutAtChecker);
                return canPutAtChecker.isLineBreakpointAvailable();
            }
        }

        return false;
    }

    private static final class LineBreakpointAvailabilityProcessor implements Processor<PsiElement> {
        private final ORLangTypes myTypes;
        private boolean myIsLineBreakpointAvailable;

        LineBreakpointAvailabilityProcessor(ORLangTypes types) {
            myTypes = types;
        }

        @Override
        public boolean process(@NotNull PsiElement element) {
            IElementType elementType = element.getNode().getElementType();

            if (elementType.equals(myTypes.WHITE_SPACE)
                    || elementType.equals(myTypes.SINGLE_COMMENT)
                    || elementType.equals(myTypes.MULTI_COMMENT)) {
                return true;
            }

            if (elementType.equals(myTypes.LET)) {
                myIsLineBreakpointAvailable = true;
                return false;
            }

            return true;
        }

        boolean isLineBreakpointAvailable() {
            return myIsLineBreakpointAvailable;
        }
    }
}
