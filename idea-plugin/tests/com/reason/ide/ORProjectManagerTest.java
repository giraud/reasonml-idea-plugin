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
import org.jetbrains.annotations.NotNull;
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
public class ORProjectManagerTest {

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
    public void testIsModuleNoMatches() {
        when(FilenameIndex.getVirtualFilesByName(any(), any(), any())).thenReturn(Collections.emptyList());
        assertFalse(ORProjectManager.isBsProject(mockProject));
        assertFalse(ORProjectManager.isDuneProject(mockProject));
        assertFalse(ORProjectManager.isEsyProject(mockProject));
    }

    @Test
    public void testIsBsProject() {
        MockVirtualFile mockBsConfig = new MockVirtualFile(BsConfigJsonFileType.getDefaultFilename());
        registerMockFile(mockBsConfig);
        assertTrue(ORProjectManager.isBsProject(mockProject));
    }

    @Test
    public void testIsEsyProject() {
        MockVirtualFile mockEsyConfig = new MockVirtualFile(EsyPackageJsonFileType.getDefaultFilename());
        registerMockFile(mockEsyConfig);

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
        registerMockFile(mockDuneFile);
        assertTrue(ORProjectManager.isDuneProject(mockProject));
        registerMockFile(mockDuneProjectFile);
        assertTrue(ORProjectManager.isDuneProject(mockProject));
        registerMockFile(mockJBuilderFile);
        assertTrue(ORProjectManager.isDuneProject(mockProject));
    }

    @Test
    public void testFindBsConfigurationFiles_sortedByFileDepth() {
        String mockFileName = BsConstants.BS_CONFIG_FILENAME;
        MockVirtualFile mockBsConfigFileParent = new MockVirtualFile(mockFileName);
        MockVirtualFile mockBsConfigFile = new MockVirtualFile(mockFileName);
        MockVirtualFile mockBsConfigFileChild = new MockVirtualFile(mockFileName);
        // set up mock file hierarchy
        mockBsConfigFile.setParent(mockBsConfigFileParent);
        mockBsConfigFileChild.setParent(mockBsConfigFile);

        MockVirtualFile[] mocksUnsorted = { mockBsConfigFile, mockBsConfigFileChild, mockBsConfigFileParent };
        MockVirtualFile[] mocksSorted = { mockBsConfigFileParent, mockBsConfigFile, mockBsConfigFileChild };

        when(FilenameIndex.getVirtualFilesByName(mockProject, mockFileName, mockScope))
                .thenReturn(Arrays.asList(mocksUnsorted));

        LinkedHashSet<VirtualFile> bsConfigurationFiles = ORProjectManager.findBsConfigurationFiles(mockProject);
        assertMocksSorted(bsConfigurationFiles, mocksSorted);
    }

    @Test
    public void testFindDuneConfigurationFiles_sortedByFileType() {
        MockVirtualFile mockDuneFile = new MockVirtualFile(DuneConstants.DUNE_FILENAME);
        MockVirtualFile mockDuneProjectFile = new MockVirtualFile(DuneConstants.DUNE_PROJECT_FILENAME);
        MockVirtualFile mockJBuilderFile = new MockVirtualFile(DuneConstants.LEGACY_JBUILDER_FILENAME);

        MockVirtualFile[] mocksUnsorted = { mockJBuilderFile, mockDuneFile, mockDuneProjectFile };
        MockVirtualFile[] mocksSorted = { mockDuneProjectFile, mockDuneFile, mockJBuilderFile };

        // test prioritizing of dune file types by registering mocks to be returned out of order
        for (MockVirtualFile mockFile : mocksUnsorted) {
            registerMockFile(mockFile);
        }

        LinkedHashSet<VirtualFile> duneConfigurationFiles = ORProjectManager.findDuneConfigurationFiles(mockProject);
        assertMocksSorted(duneConfigurationFiles, mocksSorted);
    }

    @Test
    public void testFindDuneConfigurationFiles_sortedByFileDepth() {
        String mockFileName = DuneConstants.DUNE_FILENAME;
        MockVirtualFile mockDuneFileParent = new MockVirtualFile(mockFileName);
        MockVirtualFile mockDuneFile = new MockVirtualFile(mockFileName);
        MockVirtualFile mockDuneFileChild = new MockVirtualFile(mockFileName);
        // set up mock file hierarchy
        mockDuneFile.setParent(mockDuneFileParent);
        mockDuneFileChild.setParent(mockDuneFile);

        MockVirtualFile[] mocksUnsorted = { mockDuneFile, mockDuneFileChild, mockDuneFileParent };
        MockVirtualFile[] mocksSorted = { mockDuneFileParent, mockDuneFile, mockDuneFileChild };

        when(FilenameIndex.getVirtualFilesByName(mockProject, mockFileName, mockScope))
                .thenReturn(Arrays.asList(mocksUnsorted));

        LinkedHashSet<VirtualFile> duneConfigurationFiles = ORProjectManager.findDuneConfigurationFiles(mockProject);
        assertMocksSorted(duneConfigurationFiles, mocksSorted);
    }

    @Test
    public void testFindEsyConfigurationFiles_sortedByFileDepth() {
        String mockFileName = EsyConstants.ESY_CONFIG_FILENAME;
        MockVirtualFile mockEsyConfigFileParent = new MockVirtualFile(mockFileName);
        MockVirtualFile mockEsyConfigFile = new MockVirtualFile(mockFileName);
        MockVirtualFile mockEsyConfigFileChild = new MockVirtualFile(mockFileName);
        // set up mock file hierarchy
        mockEsyConfigFile.setParent(mockEsyConfigFileParent);
        mockEsyConfigFileChild.setParent(mockEsyConfigFile);

        MockVirtualFile[] mocksUnsorted = { mockEsyConfigFile, mockEsyConfigFileChild, mockEsyConfigFileParent };
        MockVirtualFile[] mocksSorted = { mockEsyConfigFileParent, mockEsyConfigFile, mockEsyConfigFileChild };

        when(FilenameIndex.getVirtualFilesByName(mockProject, mockFileName, mockScope))
                .thenReturn(Arrays.asList(mocksUnsorted));

        when(EsyPackageJson.isEsyPackageJson(any())).thenReturn(true);

        LinkedHashSet<VirtualFile> esyConfigurationFiles = ORProjectManager.findEsyConfigurationFiles(mockProject);
        assertMocksSorted(esyConfigurationFiles, mocksSorted);
    }

    private void registerMockFile(VirtualFile mockFile) {
        registerMockFile(mockFile.getName(), mockFile);
    }

    private void registerMockFile(String filename, VirtualFile fileToReturn) {
        when(FilenameIndex.getVirtualFilesByName(mockProject, filename, mockScope))
                .thenReturn(Collections.singletonList(fileToReturn));
    }

    private void assertMocksSorted(LinkedHashSet<VirtualFile> actualOrder, VirtualFile[] expectedOrder) {
        assertEquals(expectedOrder.length, actualOrder.size());
        Iterator<VirtualFile> iterator = actualOrder.iterator();
            for (VirtualFile expected : expectedOrder) {
            VirtualFile next = iterator.next();
            assertEquals(expected, next);
        }
    }
}
