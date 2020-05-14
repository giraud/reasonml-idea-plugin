package com.reason.bs;

import com.intellij.mock.MockVirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.ORProjectManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Optional;

import static com.reason.bs.BsConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

public class BsPlatformTest {

    private static final String MOCK_ROOT = "MOCK_DIRECTORY";

    @Mock
    private Project mockProject;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
<<<<<<< HEAD
    public void testFindBsPlatformDirectory() {
=======
    public void findBsPlatformDirectory() {
>>>>>>> 35c3b4ba... Fixed bug with BsPlatform::findBinaryInBsPlatform. Added test.
        MockVirtualFile mockBsConfigFile = MockVirtualFile.file(BS_CONFIG_FILENAME);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME);
        MockVirtualFile mockNodeModulesDirectory = MockVirtualFile.dir("node_modules", mockBsPlatformDirectory);
        MockVirtualFile mockSourceFile = MockVirtualFile.dir(MOCK_ROOT, mockBsConfigFile, mockNodeModulesDirectory);
        Optional<VirtualFile> bsPlatformDirectory = BsPlatform.findBsPlatformDirectory(mockProject, mockSourceFile);
        assertTrue(bsPlatformDirectory.isPresent());
        assertEquals(mockBsPlatformDirectory, bsPlatformDirectory.get());
    }

    @Test
<<<<<<< HEAD
    public void testFindBsbExecutable_platformSpecificBinary() {
        MockVirtualFile mockBsConfigFile = MockVirtualFile.file(BS_CONFIG_FILENAME);
        MockVirtualFile mockBsbExecutable = MockVirtualFile.file(BSB_EXECUTABLE_NAME + ".exe");
        MockVirtualFile mockPlatformDirectory = MockVirtualFile.dir(getOsBsPrefix(), mockBsbExecutable);
=======
    public void findBsbExecutable_platformSpecificBinary() {
        String osBsPrefix = BsPlatform.getOsBsPrefix()
                .orElseThrow(() -> new RuntimeException("Unable to determine OS BS prefix."));
        MockVirtualFile mockBsConfigFile = MockVirtualFile.file(BS_CONFIG_FILENAME);
        MockVirtualFile mockBsbExecutable = MockVirtualFile.file(BSB_EXECUTABLE_NAME + ".exe");
        MockVirtualFile mockPlatformDirectory = MockVirtualFile.dir(osBsPrefix, mockBsbExecutable);
>>>>>>> 35c3b4ba... Fixed bug with BsPlatform::findBinaryInBsPlatform. Added test.
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME, mockPlatformDirectory);
        MockVirtualFile mockNodeModulesDirectory = MockVirtualFile.dir("node_modules", mockBsPlatformDirectory);
        MockVirtualFile mockSourceFile = MockVirtualFile.dir(MOCK_ROOT, mockBsConfigFile, mockNodeModulesDirectory);
        Optional<VirtualFile> bsbExecutable = BsPlatform.findBsbExecutable(mockProject, mockSourceFile);
        assertTrue(bsbExecutable.isPresent());
        assertEquals(mockBsbExecutable, bsbExecutable.get());
    }
<<<<<<< HEAD

    @Test
    public void testFindBsbExecutable_platformAgnosticWrapper() {
        String bsbWrapperFilename = BSB_EXECUTABLE_NAME + BsPlatform.getOsBinaryWrapperExtension();
        MockVirtualFile mockBsConfigFile = MockVirtualFile.file(BS_CONFIG_FILENAME);
        MockVirtualFile mockBsbExecutableWrapper = MockVirtualFile.file(bsbWrapperFilename);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME, mockBsbExecutableWrapper);
        MockVirtualFile mockNodeModulesDirectory = MockVirtualFile.dir("node_modules", mockBsPlatformDirectory);
        MockVirtualFile mockSourceFile = MockVirtualFile.dir(MOCK_ROOT, mockBsConfigFile, mockNodeModulesDirectory);
        Optional<VirtualFile> bsbExecutableWrapper = BsPlatform.findBsbExecutable(mockProject, mockSourceFile);
        assertTrue(bsbExecutableWrapper.isPresent());
        assertEquals(mockBsbExecutableWrapper, bsbExecutableWrapper.get());
    }

    private static String getOsBsPrefix() {
        return BsPlatform.getOsBsPrefix()
                .orElseThrow(() -> new RuntimeException("Unable to determine OS BS prefix."));
    }
=======
>>>>>>> 35c3b4ba... Fixed bug with BsPlatform::findBinaryInBsPlatform. Added test.
}