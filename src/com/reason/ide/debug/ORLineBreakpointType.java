package com.reason.ide.debug;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.reason.ide.files.FileHelper;
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
    public ORLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new ORLineBreakpointProperties();
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        FileType fileType = file.getFileType();
        if (FileHelper.isOCaml(fileType) || FileHelper.isReason(fileType)) {
            // todo
        }
        return false;
    }
}
