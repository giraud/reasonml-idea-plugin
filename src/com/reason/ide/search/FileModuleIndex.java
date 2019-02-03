package com.reason.ide.search;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.util.containers.hash.HashMap;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
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
    private static final int VERSION = 3;

    public static final class FileModuleDataExternalizer implements DataExternalizer<FileModuleData> {
        @Override
        public void save(@NotNull DataOutput out, FileModuleData value) throws IOException {
            out.writeBoolean(value.isInterface());
            out.writeUTF(value.getModuleName());
        }

        @Override
        public FileModuleData read(@NotNull DataInput in) throws IOException {
            boolean isInterface = in.readBoolean();
            String moduleName = in.readUTF();
            return new FileModuleData(moduleName, isInterface);
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
                String moduleName = psiFile.asModuleName();
                map.put(moduleName, new FileModuleData(moduleName, psiFile.isInterface()));
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
