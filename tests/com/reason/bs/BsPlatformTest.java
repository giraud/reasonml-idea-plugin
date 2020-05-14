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

import static com.reason.bs.BsConstants.BS_CONFIG_FILENAME;
import static com.reason.bs.BsConstants.BS_PLATFORM_DIRECTORY_NAME;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

public class BsPlatformTest {

    @Mock
    private Project mockProject;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void findBsPlatformDirectory() {
        MockVirtualFile mockBsConfigFile = new MockVirtualFile(BS_CONFIG_FILENAME);
        MockVirtualFile mockBsPlatformDirectory = MockVirtualFile.dir(BS_PLATFORM_DIRECTORY_NAME);
        MockVirtualFile mockNodeModulesDirectory = MockVirtualFile.dir("node_modules", mockBsPlatformDirectory);
        MockVirtualFile mockSourceFile = MockVirtualFile.dir("MOCK_DIR", mockBsConfigFile, mockNodeModulesDirectory);
        Optional<VirtualFile> bsPlatformDirectory = BsPlatform.findBsPlatformDirectory(mockProject, mockSourceFile);
        assertTrue(bsPlatformDirectory.isPresent());
        assertEquals(mockBsPlatformDirectory, bsPlatformDirectory.get());
    }
}