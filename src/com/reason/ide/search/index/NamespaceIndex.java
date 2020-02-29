package com.reason.ide.search.index;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.reason.Log;
import com.reason.Platform;
import com.reason.StringUtil;
import com.reason.bs.BsConfig;
import com.reason.bs.BsConfigReader;
import com.reason.ide.files.DuneFile;
import com.reason.ide.files.DuneFileType;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiStanza;

public class NamespaceIndex extends ScalarIndexExtension<String> {

    private static final ID<String, Void> NAME = ID.create("reason.index.bsconfig");
    private static final int VERSION = 5;
    private static final Log LOG = Log.create("index.namespace");
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
                List<PsiStanza> stanzas = ORUtil.findImmediateChildrenOfClass(duneFile, PsiStanza.class);
                for (PsiStanza stanza : stanzas) {
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
                    VirtualFile baseRoot = Platform.findORPackageJsonContentRoot(inputData.getProject());
                    if (baseRoot != null) {
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
        return file -> file.getFileType() instanceof DuneFileType || (file.getFileType() instanceof JsonFileType && "bsconfig.json".equals(file.getName()));
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
