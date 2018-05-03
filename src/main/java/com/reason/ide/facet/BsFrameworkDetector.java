package com.reason.ide.facet;

import com.intellij.facet.FacetType;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;

public class BsFrameworkDetector extends FacetBasedFrameworkDetector {

    protected BsFrameworkDetector() {
        super("bucklescript");
    }

    @NotNull
    @Override
    public FacetType<BsFacet, BsFacetConfiguration> getFacetType() {
        return BsFacet.getFacetType();
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return JsonFileType.INSTANCE;
    }

    @NotNull
    @Override
    public ElementPattern<FileContent> createSuitableFilePattern() {
        return FileContentPattern.fileContent().withName("bsconfig.json");
    }
}
