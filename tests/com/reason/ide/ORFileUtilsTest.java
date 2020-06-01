package com.reason.ide;

import com.intellij.mock.MockVirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.bs.BsConstants;
import com.reason.dune.DuneConstants;
import com.reason.esy.EsyConstants;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.files.BsConfigJsonFileType;
import com.reason.ide.files.EsyPackageJsonFileType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EsyPackageJson.class, FilenameIndex.class, GlobalSearchScope.class})
public class ORFileUtilsTest {

    @Mock
    GlobalSearchScope mockScope;

    @Mock
    private Project mockProject;

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(EsyPackageJson.class);
        mockStatic(FilenameIndex.class);
        mockStatic(GlobalSearchScope.class);
        when(GlobalSearchScope.allScope(mockProject)).thenReturn(mockScope);
    }

    @Test
    public void testFindAncestorRecursive_startAtRootOfFileSystem() {
        VirtualFile mockFile = spy(VirtualFile.class);
        when(mockFile.isDirectory()).thenReturn(false);
        when(mockFile.getParent()).thenReturn(null);
        Optional<VirtualFile> ancestor = ORFileUtils.findAncestorRecursive(mockProject, "", mockFile);
        verify(mockFile).isDirectory();
        verify(mockFile).getParent();
        assertFalse(ancestor.isPresent());
    }

    @Test
    public void testFindAncestorRecursive_hitRootOfFileSystem() {
        String mockProjectBasePath = "mock-path";
        when(mockProject.getBasePath()).thenReturn(mockProjectBasePath);
        VirtualFile mockStart = spy(VirtualFile.class);
        when(mockStart.isDirectory()).thenReturn(true);
        when(mockStart.getPath()).thenReturn(mockProjectBasePath);
        Optional<VirtualFile> ancestor = ORFileUtils.findAncestorRecursive(mockProject, "", mockStart);
        assertFalse(ancestor.isPresent());
    }

    @Test
    public void testFindAncestorRecursive_targetFound() {
        VirtualFile mockTarget = new MockVirtualFile("mock-target");
        String targetName = mockTarget.getName();
        VirtualFile mockStart = spy(VirtualFile.class);
        when(mockStart.isDirectory()).thenReturn(true);
        when(mockStart.findChild(targetName)).thenReturn(mockTarget);
        Optional<VirtualFile> ancestor = ORFileUtils.findAncestorRecursive(mockProject, targetName, mockStart);
        assertTrue(ancestor.isPresent());
        assertEquals(mockTarget, ancestor.get());
    }
}
