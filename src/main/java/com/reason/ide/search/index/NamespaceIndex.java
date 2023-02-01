package com.reason.ide.search.index;

import com.intellij.json.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.indexing.*;
import com.intellij.util.io.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
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

    @NotNull
    public static NamespaceIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
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
                BsConfig configFile = BsConfigReader.read(dataFile);
                if (configFile.hasNamespace()) {
                    VirtualFile contentRoot = ORProjectManager.findFirstBsContentRoot(inputData.getProject());
                    if (contentRoot != null) {
                        String namespace = configFile.getNamespace();
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

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file ->
                file.getFileType() instanceof DuneFileType
                        || (file.getFileType() instanceof JsonFileType
                        && "bsconfig.json".equals(file.getName()));
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
