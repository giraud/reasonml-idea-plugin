package com.reason.ide.search.index;

import com.intellij.openapi.vfs.*;
import com.intellij.psi.util.*;
import com.intellij.util.indexing.*;
import com.intellij.util.io.*;
import com.reason.comp.bs.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public class FileModuleIndex extends FileBasedIndexExtension<String, FileModuleData> {

    private static final ID<String, FileModuleData> NAME = ID.create("reason.module.fileIndex");
    private static final int VERSION = 2;
    private static final Log LOG = Log.create("index.file");

    private static final DataExternalizer<FileModuleData> EXTERNALIZER = new FileModuleDataExternalizer();

    public static @Nullable FileModuleIndex getInstance() {
        return EXTENSION_POINT_NAME.findExtension(FileModuleIndex.class);
    }

    @Override
    public @NotNull ID<String, FileModuleData> getName() {
        return NAME;
    }

    static final class FileModuleDataExternalizer implements DataExternalizer<FileModuleData> {
        @Override
        public void save(@NotNull DataOutput out, @NotNull FileModuleData value) throws IOException {
            out.writeBoolean(value.isOCaml());
            out.writeBoolean(value.isRescript());
            out.writeBoolean(value.isInterface());
            out.writeBoolean(value.isComponent());
            out.writeBoolean(value.hasComponents());
            out.writeUTF(value.getPath());
            out.writeUTF(value.getNamespace());
            out.writeUTF(value.getModuleName());
            out.writeUTF(value.getFullName());
        }

        @NotNull
        @Override
        public FileModuleData read(@NotNull DataInput in) throws IOException {
            boolean isOCaml = in.readBoolean();
            boolean isRescript = in.readBoolean();
            boolean isInterface = in.readBoolean();
            boolean isComponent = in.readBoolean();
            boolean hasComponents = in.readBoolean();
            String path = in.readUTF();
            String namespace = in.readUTF();
            String moduleName = in.readUTF();
            String fullName = in.readUTF();
            return new FileModuleData(path, fullName, namespace, moduleName, isOCaml, isRescript, isInterface, isComponent, hasComponents);
        }
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
            Map<String, FileModuleData> map = new HashMap<>();

            FileBase psiFile = (FileBase) inputData.getPsiFile();
            String moduleName = psiFile.getModuleName();

            String namespace = "";

            VirtualFile bsconfigFile = BsPlatform.findBsConfig(inputData.getProject(), inputData.getFile());
            if (bsconfigFile != null) {
                VirtualFile parent = bsconfigFile.getParent();
                boolean useExternalAsSource = "bs-platform".equals(parent.getName());
                BsConfig bsConfig = BsConfigReader.read(bsconfigFile, useExternalAsSource);
                //if (!bsConfig.isInSources(inputData.getFile())) {
                //LOG.debug("Indexing external file", (PsiFile) psiFile);
                //        if (LOG.isDebugEnabled()) {
                //            LOG.debug("»» SKIP " + inputData.getFile() + " / bsconf: " + bsconfigFile);
                //        }
                //        return Collections.emptyMap();
                //}
                namespace = bsConfig.getNamespace();
            }

            boolean hasComponents = PsiTreeUtil.findChildrenOfType(psiFile, RPsiInnerModule.class).stream().anyMatch(RPsiInnerModule::isComponent);
            boolean isOCaml = FileHelper.isOCaml(inputData.getFileType());
            boolean isRescript = FileHelper.isRescript(inputData.getFileType());

            FileModuleData value = new FileModuleData(inputData.getFile(), namespace, moduleName, isOCaml, isRescript, psiFile.isInterface(), psiFile.isComponent(), hasComponents);
            if (LOG.isDebugEnabled()) {
                LOG.debug("indexing " + Platform.getRelativePathToModule(inputData.getPsiFile()) + ": " + value);
            }

            map.put(moduleName, value);
            if (!namespace.isEmpty()) {
                map.put(namespace + "." + moduleName, value);
            }

            return map;
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
        return new DefaultFileTypeSpecificInputFilter(
                RmlFileType.INSTANCE, RmlInterfaceFileType.INSTANCE,
                OclFileType.INSTANCE, OclInterfaceFileType.INSTANCE,
                ResFileType.INSTANCE, ResInterfaceFileType.INSTANCE);
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
