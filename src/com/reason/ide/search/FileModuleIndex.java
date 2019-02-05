package com.reason.ide.search;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.hash.HashMap;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.Platform;
import com.reason.build.bs.BsConfig;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class FileModuleIndex extends FileBasedIndexExtension<String, FileModuleData> {

    private static final ID<String, FileModuleData> NAME = ID.create("reason.index.fileModule");
    private static final DataExternalizer<FileModuleData> EXTERNALIZER = new FileModuleDataExternalizer();
    private static final int VERSION = 31;
    private static final Log LOG = Log.create("index.file");

    public static final class FileModuleDataExternalizer implements DataExternalizer<FileModuleData> {
        @Override
        public void save(@NotNull DataOutput out, FileModuleData value) throws IOException {
            out.writeBoolean(value.isInterface());
            out.writeBoolean(value.isComponent());
            out.writeUTF(value.getNamespace());
            out.writeUTF(value.getModuleName());
        }

        @Override
        public FileModuleData read(@NotNull DataInput in) throws IOException {
            boolean isInterface = in.readBoolean();
            boolean isComponent = in.readBoolean();
            String namespace = in.readUTF();
            String moduleName = in.readUTF();
            return new FileModuleData(namespace, moduleName, isInterface, isComponent);
        }
    }

    private final FileBasedIndex.InputFilter m_inputFilter = file -> {
        FileType fileType = file.getFileType();
        return FileHelper.isReason(fileType) || FileHelper.isOCaml(fileType);
    };

    @NotNull
    @Override
    public ID<String, FileModuleData> getName() {
        return NAME;
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
                Map<String, FileModuleData> map = new HashMap<>();
                FileBase psiFile = (FileBase) inputData.getPsiFile();

                String namespace = "";
                VirtualFile bsConfigFromFile = Platform.findBsConfigFromFile(inputData.getProject(), inputData.getFile());
                if (bsConfigFromFile != null) {
                    Document document = FileDocumentManager.getInstance().getDocument(bsConfigFromFile);
                    String text = document.getText();
                    BsConfig bsConfig = BsConfig.read(bsConfigFromFile.getParent(), bsConfigFromFile.getPath(), text);
                    if (!"bs-platform".equals(bsConfig.getName())) {
                        if (!bsConfig.isInSources(inputData.getFile())) {
                            //System.out.println("»» SKIP " + inputData.getFile() + " / bsconf: " + bsConfigFromFile);
                            return Collections.emptyMap();
                        }

                        System.out.println("Indexing " + inputData.getFile() + " / sources: [" + Joiner.join(", ", bsConfig.getSources()) + "] / bsconf: " + bsConfigFromFile);
                        namespace = bsConfig.getNamespace();
                    }
                }
                String moduleName = psiFile.asModuleName();

                FileModuleData value = new FileModuleData(namespace, moduleName, psiFile.isInterface(), psiFile.isComponent());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("indexing " + inputData.getFile() + ": " + value);
                }

                map.put(moduleName, value);
                if (!namespace.isEmpty()) {
                    map.put(namespace + "_" + moduleName, value);
                }

                return map;
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
