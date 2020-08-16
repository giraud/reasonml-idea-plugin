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
import static org.mockito.Mockito.when;
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
        MockVirtualFile mockSourceDirectory = MockVirtualFile.dir(MOCK_ROOT, mockBsConfigFile, mockNodeModulesDirectory);
        Optional<VirtualFile> bsPlatformDirectory = BsPlatform.findBsPlatformDirectory(mockProject, mockSourceDirectory);
        assertTrue(bsPlatformDirectory.isPresent());
        assertEquals(mockBsPlatformDirectory, bsPlatformDirectory.get());
    }

    @Test
    public void testFindBsbExecutable_platformSpecificBinary() {
        MockVirtualFile mockSourceFile = MockVirtualFile.file("mock-source-file.bs");
        MockVirtualFile mockSourceDirectory = MockVirtualFile.dir("src", mockSourceFile);

        MockVirtualFile mockBsbExecutable = MockVirtualFile.file(BSB_EXECUTABLE_NAME + ".exe");
        MockVirtualFile mockPlatformDirectory = MockVirtualFile.dir(getOsBsPrefix(), mockBsbExecutable);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME, mockPlatformDirectory);
        MockVirtualFile mockNodeModulesDirectory = MockVirtualFile.dir("node_modules", mockBsPlatformDirectory);

        MockVirtualFile mockBsConfigFile = MockVirtualFile.file(BS_CONFIG_FILENAME);

        MockVirtualFile mockRootDirectory = MockVirtualFile.dir(MOCK_ROOT, mockBsConfigFile, mockNodeModulesDirectory, mockSourceDirectory);
        when(mockProject.getBasePath()).thenReturn(mockRootDirectory.getPath());

        Optional<VirtualFile> bsbExecutable = BsPlatform.findBsbExecutable(mockProject, mockSourceFile);
        assertTrue(bsbExecutable.isPresent());
        assertEquals(mockBsbExecutable, bsbExecutable.get());
    }

    @Test
    public void testFindBsbExecutable_platformAgnosticWrapper() {
        MockVirtualFile mockSourceFile = MockVirtualFile.file("mock-source-file.bs");
        MockVirtualFile mockSourceDirectory = MockVirtualFile.dir("src", mockSourceFile);

        String bsbWrapperFilename = BSB_EXECUTABLE_NAME + BsPlatform.getOsBinaryWrapperExtension();
        MockVirtualFile mockBsbExecutableWrapper = MockVirtualFile.file(bsbWrapperFilename);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME, mockBsbExecutableWrapper);
        MockVirtualFile mockNodeModulesDirectory = MockVirtualFile.dir("node_modules", mockBsPlatformDirectory);

        MockVirtualFile mockBsConfigFile = MockVirtualFile.file(BS_CONFIG_FILENAME);

        MockVirtualFile mockRootDirectory = MockVirtualFile.dir(MOCK_ROOT, mockBsConfigFile, mockNodeModulesDirectory, mockSourceDirectory);
        when(mockProject.getBasePath()).thenReturn(mockRootDirectory.getPath());

        Optional<VirtualFile> bsbExecutableWrapper = BsPlatform.findBsbExecutable(mockProject, mockBsPlatformDirectory);
        assertTrue(bsbExecutableWrapper.isPresent());
        assertEquals(mockBsbExecutableWrapper, bsbExecutableWrapper.get());
    }

    private static String getOsBsPrefix() {
        return BsPlatform.getOsBsPrefix()
                .orElseThrow(() -> new RuntimeException("Unable to determine OS BS prefix."));
    }
}
