package com.reason.ide.debug;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.reason.ide.files.FileHelper;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlTypes;

public class ORLineBreakpointType extends XLineBreakpointType<ORLineBreakpointProperties> {
    private static final String ID = "OCamlLineBreakpoint";
    private static final String NAME = "Line breakpoint";

    protected ORLineBreakpointType() {
        super(ID, NAME);
    }

    @Nullable
    @Override
    public ORLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new ORLineBreakpointProperties();
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        FileType fileType = file.getFileType();
        if (FileHelper.isOCaml(fileType) || FileHelper.isReason(fileType)) {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                LineBreakpointAvailabilityProcessor canPutAtChecker = new LineBreakpointAvailabilityProcessor(
                        FileHelper.isOCaml(fileType) ? OclTypes.INSTANCE : RmlTypes.INSTANCE);
                XDebuggerUtil.getInstance().iterateLine(project, document, line, canPutAtChecker);
                return canPutAtChecker.isLineBreakpointAvailable();
            }
        }

        return false;
    }

    private static final class LineBreakpointAvailabilityProcessor implements Processor<PsiElement> {
        private final ORTypes m_types;
        private boolean m_isLineBreakpointAvailable;

        LineBreakpointAvailabilityProcessor(ORTypes types) {
            m_types = types;
        }

        @Override
        public boolean process(@NotNull PsiElement element) {
            IElementType elementType = element.getNode().getElementType();

            if (elementType.equals(TokenType.WHITE_SPACE) || elementType.equals(m_types.SINGLE_COMMENT) || elementType.equals(m_types.MULTI_COMMENT)) {
                return true;
            }

            if (elementType.equals(m_types.LET)) {
                m_isLineBreakpointAvailable = true;
                return false;
            }

            return true;
        }

        boolean isLineBreakpointAvailable() {
            return m_isLineBreakpointAvailable;
        }
    }
}
