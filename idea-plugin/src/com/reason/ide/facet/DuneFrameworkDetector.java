package com.reason.ide.facet;

import com.intellij.facet.FacetType;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import com.reason.ide.files.DuneFileType;
import org.jetbrains.annotations.NotNull;

public class DuneFrameworkDetector
    extends FacetBasedFrameworkDetector<DuneFacet, DuneFacetConfiguration> {

  protected DuneFrameworkDetector() {
    super(DuneFacet.ID_NAME);
  }

  @NotNull
  @Override
  public FacetType<DuneFacet, DuneFacetConfiguration> getFacetType() {
    return DuneFacet.getFacetType();
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return DuneFileType.INSTANCE;
  }

  @NotNull
  @Override
  public ElementPattern<FileContent> createSuitableFilePattern() {
    return FileContentPattern.fileContent().withName("dune-project");
  }
}
