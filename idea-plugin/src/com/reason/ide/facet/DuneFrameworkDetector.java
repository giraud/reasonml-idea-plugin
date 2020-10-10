package com.reason.ide.facet;

import com.intellij.facet.FacetType;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import com.reason.ide.files.DuneFileType;
import org.jetbrains.annotations.NotNull;

public class DuneFrameworkDetector extends FacetBasedFrameworkDetector<DuneFacet, DuneFacetConfiguration> {
    protected DuneFrameworkDetector() {
        super(DuneFacet.ID_NAME);
    }

    @Override
    public @NotNull FacetType<DuneFacet, DuneFacetConfiguration> getFacetType() {
        return DuneFacet.getFacetType();
    }

    @Override
    public @NotNull FileType getFileType() {
        return DuneFileType.INSTANCE;
    }

    @Override
    public @NotNull ElementPattern<FileContent> createSuitableFilePattern() {
        return FileContentPattern.fileContent().withName("dune-project");
    }
}
