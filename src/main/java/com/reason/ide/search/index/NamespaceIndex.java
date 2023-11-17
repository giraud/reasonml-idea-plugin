package com.reason.ide.search.index;

import com.intellij.openapi.vfs.*;
import com.intellij.util.indexing.*;
import com.intellij.util.io.*;
import com.reason.*;
import com.reason.comp.bs.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class NamespaceIndex extends ScalarIndexExtension<String> {

    private static final int VERSION = 6;

    private static final Log LOG = Log.create("index.namespace");

    private static final ID<String, Void> NAME = ID.create("reason.index.bsconfig");
    private static final NamespaceIndex INSTANCE = new NamespaceIndex();

    public static @NotNull NamespaceIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull ID<String, Void> getName() {
        return NAME;
    }

    @Override
    public @NotNull DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            VirtualFile dataFile = inputData.getFile();
            if (inputData.getFileType() instanceof DuneFileType) {
                DuneFile duneFile = (DuneFile) inputData.getPsiFile();
                List<RPsiDuneStanza> stanzas = ORUtil.findImmediateChildrenOfClass(duneFile, RPsiDuneStanza.class);
                for (RPsiDuneStanza stanza : stanzas) {
                    if ("library".equals(stanza.getName())) {
                        VirtualFile parent = dataFile.getParent();
                        String namespace = StringUtil.toFirstUpper(parent.getName());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Indexing " + dataFile + " with namespace " + namespace);
                        }
                        return Collections.singletonMap(namespace, null);
                    }
                }
            } else {
                BsConfig config = BsConfigReader.read(dataFile);
                if (config.hasNamespace()) {
                    VirtualFile contentRoot = BsPlatform.findConfigFiles(inputData.getProject()).stream().findFirst().orElse(null);
                    if (contentRoot != null) {
                        String namespace = config.getNamespace();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Indexing " + dataFile + " with namespace " + namespace);
                        }
                        return Collections.singletonMap(namespace, null);
                    }
                }
            }

            return Collections.emptyMap();
        };
    }

    @Override
    public @NotNull KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @Override
    public @NotNull FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getFileType() instanceof DuneFileType || FileHelper.isRescriptConfigJson(file);
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return VERSION;
    }
}
