package com.reason.ide.search.index;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.reason.Log;
import com.reason.Platform;
import com.reason.StringUtil;
import com.reason.build.bs.BsConfig;
import com.reason.ide.files.DuneFile;
import com.reason.ide.files.DuneFileType;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class NamespaceIndex extends ScalarIndexExtension<String> {

    private static final ID<String, Void> NAME = ID.create("reason.index.bsconfig");
    private static final int VERSION = 4;
    private static final Log LOG = Log.create("index.namespace");

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
                PsiElement firstChild = duneFile.getFirstChild();
                PsiElement nextSibling = firstChild == null ? null : firstChild.getNextSibling(); // todo: better
                if (nextSibling != null && nextSibling.getNode().getElementType() == DuneTypes.INSTANCE.LIBRARY) {
                    VirtualFile parent = dataFile.getParent();
                    String namespace = StringUtil.toFirstUpper(parent.getName());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Indexing " + dataFile + " with namespace " + namespace);
                    }
                    return Collections.singletonMap(namespace, null);
                }
            } else {
                PsiFile file = PsiManager.getInstance(inputData.getProject()).findFile(dataFile);
                if (file != null) {
                    BsConfig configFile = BsConfig.read(dataFile, file, false);
                    if (configFile.hasNamespace()) {
                        VirtualFile baseRoot = Platform.findBaseRoot(inputData.getProject());
                        VirtualFile parent = dataFile.getParent();
                        if (baseRoot.equals(parent)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Skip indexing of " + dataFile + ": in project root");
                            }
                        } else {
                            String namespace = configFile.getNamespace();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Indexing " + dataFile + " with namespace " + namespace);
                            }
                            return Collections.singletonMap(namespace, null);
                        }
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
