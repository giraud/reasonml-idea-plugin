package com.reason.ide.search.index;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.hash.HashMap;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.reason.Log;
import com.reason.Platform;
import com.reason.bs.BsConfig;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.FileModuleData;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class FileModuleIndex extends FileBasedIndexExtension<String, FileModuleData> {

    private static final DataExternalizer<FileModuleData> EXTERNALIZER = new FileModuleDataExternalizer();
    private static final int VERSION = 5;
    private static final Log LOG = Log.create("index.file");
    private static final FileModuleIndex INSTANCE = new FileModuleIndex();

    @NotNull
    public static FileModuleIndex getInstance() {
        return INSTANCE;
    }

    public static final class FileModuleDataExternalizer implements DataExternalizer<FileModuleData> {
        @Override
        public void save(@NotNull DataOutput out, FileModuleData value) throws IOException {
            out.writeBoolean(value.isOCaml());
            out.writeBoolean(value.isInterface());
            out.writeBoolean(value.isComponent());
            out.writeUTF(value.getPath());
            out.writeUTF(value.getNamespace());
            out.writeUTF(value.getModuleName());
            out.writeUTF(value.getFullname());
        }

        @Override
        public FileModuleData read(@NotNull DataInput in) throws IOException {
            boolean isOCaml = in.readBoolean();
            boolean isInterface = in.readBoolean();
            boolean isComponent = in.readBoolean();
            String path = in.readUTF();
            String namespace = in.readUTF();
            String moduleName = in.readUTF();
            String fullname = in.readUTF();
            return new FileModuleData(path, fullname, namespace, moduleName, isOCaml, isInterface, isComponent);
        }
    }

    private final FileBasedIndex.InputFilter m_inputFilter = file -> {
        FileType fileType = file.getFileType();
        return FileHelper.isReason(fileType) || FileHelper.isOCaml(fileType);
    };

    @NotNull
    @Override
    public ID<String, FileModuleData> getName() {
        return IndexKeys.FILE_MODULE;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public DataIndexer<String, FileModuleData, FileContent> getIndexer() {
        return inputData -> {
            if (FileHelper.isReason(inputData.getFileType()) || FileHelper.isOCaml(inputData.getFileType())) {
                PsiFile inputPsiFile = inputData.getPsiFile();
                if (inputPsiFile instanceof FileBase) {
                    FileBase psiFile = (FileBase) inputPsiFile;
                    Map<String, FileModuleData> map = new HashMap<>();

                    String namespace = "";
                    VirtualFile bsConfigFromFile = Platform.findBsConfigFromFile(inputData.getProject(), inputData.getFile());
                    String path = inputData.getFile().getPath();
                    if (bsConfigFromFile != null) {
                        VirtualFile parent = bsConfigFromFile.getParent();
                        boolean useExternalAsSource = "bs-platform".equals(parent.getName());
                        PsiManager psiManager = PsiManager.getInstance(inputData.getProject());

                        PsiFile configFile = psiManager.findFile(bsConfigFromFile);
                        if (configFile != null) {
                            BsConfig bsConfig = BsConfig.read(parent, configFile, useExternalAsSource);
                            if (!bsConfig.isInSources(inputData.getFile())) {
                                LOG.debug("»» SKIP " + inputData.getFile() + " / bsconf: " + bsConfigFromFile);
                                return Collections.emptyMap();
                            }

                            //System.out.println("Indexing " + inputData.getFile() + " / sources: [" + Joiner.join(", ", bsConfig.getSources()) + "] / bsconf: " + bsConfigFromFile);
                            namespace = bsConfig.getNamespace();
                        }
                    }
                    String moduleName = psiFile.asModuleName();

                    FileModuleData value = new FileModuleData(inputData.getFile(), namespace, moduleName, FileHelper.isOCaml(inputData.getFileType()), psiFile.isInterface(), psiFile.isComponent());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("indexing " + Platform.removeProjectDir(inputData.getProject(), path) + ": " + value);
                    }

                    map.put(moduleName, value);
                    if (!namespace.isEmpty()) {
                        map.put(namespace + "." + moduleName, value);
                    }

                    return map;
                }
            }
            return Collections.emptyMap();
        };
    }

    @NotNull
    @Override
    public DataExternalizer<FileModuleData> getValueExternalizer() {
        return EXTERNALIZER;
    }

    @Override
    public int getVersion() {
        return VERSION;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return m_inputFilter;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
