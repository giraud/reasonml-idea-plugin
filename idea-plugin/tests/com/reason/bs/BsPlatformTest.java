package com.reason.bs;

import com.intellij.mock.MockVirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static com.reason.bs.BsConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class BsPlatformTest {

    private static final String MOCK_ROOT = "MOCK_DIRECTORY";

    @Mock
    private Project mockProject;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testFindBsPlatformDirectory() {
        MockVirtualFile mockBsConfigFile = MockVirtualFile.file(BS_CONFIG_FILENAME);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME);
        MockVirtualFile mockNodeModulesDirectory = MockVirtualFile.dir("node_modules", mockBsPlatformDirectory);
        MockVirtualFile mockSourceFile = MockVirtualFile.dir(MOCK_ROOT, mockBsConfigFile, mockNodeModulesDirectory);
        Optional<VirtualFile> bsPlatformDirectory = BsPlatform.findBsPlatformDirectory(mockProject, mockSourceFile);
        assertTrue(bsPlatformDirectory.isPresent());
        assertEquals(mockBsPlatformDirectory, bsPlatformDirectory.get());
    }

    @Test
    public void testFindBsbExecutable_platformSpecificBinary() {
        MockVirtualFile mockBsbExecutable = MockVirtualFile.file(BSB_EXECUTABLE_NAME + ".exe");
        MockVirtualFile mockPlatformDirectory = MockVirtualFile.dir(getOsBsPrefix(), mockBsbExecutable);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME, mockPlatformDirectory);
        Optional<VirtualFile> bsbExecutable = BsPlatform.findBsbExecutable(mockBsPlatformDirectory);
        assertTrue(bsbExecutable.isPresent());
        assertEquals(mockBsbExecutable, bsbExecutable.get());
    }

    @Test
    public void testFindBsbExecutable_platformAgnosticWrapper() {
        String bsbWrapperFilename = BSB_EXECUTABLE_NAME + BsPlatform.getOsBinaryWrapperExtension();
        MockVirtualFile mockBsbExecutableWrapper = MockVirtualFile.file(bsbWrapperFilename);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME, mockBsbExecutableWrapper);
        Optional<VirtualFile> bsbExecutableWrapper = BsPlatform.findBsbExecutable(mockBsPlatformDirectory);
        assertTrue(bsbExecutableWrapper.isPresent());
        assertEquals(mockBsbExecutableWrapper, bsbExecutableWrapper.get());
    }

    private static String getOsBsPrefix() {
        return BsPlatform.getOsBsPrefix()
                .orElseThrow(() -> new RuntimeException("Unable to determine OS BS prefix."));
    }
}
