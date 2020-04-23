package com.reason.ide;

import com.intellij.mock.MockVirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.dune.DuneConstants;
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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EsyPackageJson.class, FilenameIndex.class, GlobalSearchScope.class})
public class ORProjectManagerTest {

    @Mock
    GlobalSearchScope mockScope;

    @Mock
    private Project mockProject;

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(FilenameIndex.class);
        mockStatic(GlobalSearchScope.class);
        when(GlobalSearchScope.allScope(mockProject)).thenReturn(mockScope);
    }

    @Test
    public void testIsModuleNoMatches() {
        when(FilenameIndex.getVirtualFilesByName(any(), any(), any())).thenReturn(Collections.emptyList());
        assertFalse(ORProjectManager.isBsProject(mockProject));
        assertFalse(ORProjectManager.isDuneProject(mockProject));
        assertFalse(ORProjectManager.isEsyProject(mockProject));
    }

    @Test
    public void testIsBsProject() {
        MockVirtualFile mockBsConfig = new MockVirtualFile(BsConfigJsonFileType.getDefaultFilename());
        registerFilenameMock(mockBsConfig);
        assertTrue(ORProjectManager.isBsProject(mockProject));
    }

    @Test
    public void testIsEsyProject() {
        mockStatic(EsyPackageJson.class);
        MockVirtualFile mockEsyConfig = new MockVirtualFile(EsyPackageJsonFileType.getDefaultFilename());
        registerFilenameMock(mockEsyConfig);

        OngoingStubbing<Boolean> whenIsEsyPackageJson = when(EsyPackageJson.isEsyPackageJson(mockEsyConfig));

        whenIsEsyPackageJson.thenReturn(false);
        assertFalse("File not recognized as esy package.json.", ORProjectManager.isEsyProject(mockProject));

        whenIsEsyPackageJson.thenReturn(true);
        assertTrue(ORProjectManager.isEsyProject(mockProject));
    }

    @Test
    public void testIsDuneProject_allDuneFileTypes() {
        MockVirtualFile mockDuneFile = new MockVirtualFile(DuneConstants.DUNE_FILENAME);
        MockVirtualFile mockDuneProjectFile = new MockVirtualFile(DuneConstants.DUNE_PROJECT_FILENAME);
        MockVirtualFile mockJBuilderFile = new MockVirtualFile(DuneConstants.LEGACY_JBUILDER_FILENAME);
        registerFilenameMock(mockDuneFile);
        assertTrue(ORProjectManager.isDuneProject(mockProject));
        registerFilenameMock(mockDuneProjectFile);
        assertTrue(ORProjectManager.isDuneProject(mockProject));
        registerFilenameMock(mockJBuilderFile);
        assertTrue(ORProjectManager.isDuneProject(mockProject));
    }

    @Test
    public void testFindDuneConfigurationFiles_prioritizeByFileType() {
        MockVirtualFile mockDuneFile = new MockVirtualFile(DuneConstants.DUNE_FILENAME);
        MockVirtualFile mockDuneProjectFile = new MockVirtualFile(DuneConstants.DUNE_PROJECT_FILENAME);
        MockVirtualFile mockJBuilderFile = new MockVirtualFile(DuneConstants.LEGACY_JBUILDER_FILENAME);

        MockVirtualFile[] mocksUnsorted = {
                mockJBuilderFile,
                mockDuneFile,
                mockDuneProjectFile
        };
        MockVirtualFile[] mocksSorted = {
                mockDuneProjectFile,
                mockDuneFile,
                mockJBuilderFile
        };

        // test prioritizing of dune file types by registering mocks to be returned out of order
        for (MockVirtualFile mockFile : mocksUnsorted) {
            registerFilenameMock(mockFile);
        }

        LinkedHashSet<VirtualFile> duneConfigurationFiles = ORProjectManager.findDuneConfigurationFiles(mockProject);
        assertEquals(mocksSorted.length, duneConfigurationFiles.size());
        Iterator<VirtualFile> iterator = duneConfigurationFiles.iterator();
        for (VirtualFile expected : mocksSorted) {
            VirtualFile next = iterator.next();
            assertEquals(expected, next);
        }
    }

    private void registerFilenameMock(VirtualFile mockFile) {
        registerFilenameMock(mockFile.getName(), mockFile);
    }

    private void registerFilenameMock(String filename, VirtualFile fileToReturn) {
        when(FilenameIndex.getVirtualFilesByName(mockProject, filename, mockScope))
                .thenReturn(Collections.singletonList(fileToReturn));
    }
}
