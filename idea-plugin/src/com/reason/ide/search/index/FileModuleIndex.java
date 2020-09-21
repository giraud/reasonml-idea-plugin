package com.reason.ide.search.index;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileBasedIndexExtension;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.reason.Log;
import com.reason.Platform;
import com.reason.bs.BsConfig;
import com.reason.bs.BsConfigReader;
import com.reason.bs.BsPlatform;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.FileModuleData;
import java.io.*;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public class FileModuleIndex extends FileBasedIndexExtension<String, FileModuleData> {

  private static final int VERSION = 7;

  private static final Log LOG = Log.create("index.file");

  private static final DataExternalizer<FileModuleData> EXTERNALIZER =
      new FileModuleDataExternalizer();
  private static final FileModuleIndex INSTANCE = new FileModuleIndex();

  @NotNull
  public static FileModuleIndex getInstance() {
    return INSTANCE;
  }

  static final class FileModuleDataExternalizer implements DataExternalizer<FileModuleData> {
    @Override
    public void save(@NotNull DataOutput out, @NotNull FileModuleData value) throws IOException {
      out.writeBoolean(value.isOCaml());
      out.writeBoolean(value.isInterface());
      out.writeBoolean(value.isComponent());
      out.writeUTF(value.getPath());
      out.writeUTF(value.getNamespace());
      out.writeUTF(value.getModuleName());
      out.writeUTF(value.getFullname());
    }

    @NotNull
    @Override
    public FileModuleData read(@NotNull DataInput in) throws IOException {
      boolean isOCaml = in.readBoolean();
      boolean isInterface = in.readBoolean();
      boolean isComponent = in.readBoolean();
      String path = in.readUTF();
      String namespace = in.readUTF();
      String moduleName = in.readUTF();
      String fullname = in.readUTF();
      return new FileModuleData(
          path, fullname, namespace, moduleName, isOCaml, isInterface, isComponent);
    }
  }

  private final FileBasedIndex.InputFilter m_inputFilter =
      file -> {
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
      if (FileHelper.isReason(inputData.getFileType())
          || FileHelper.isOCaml(inputData.getFileType())) {
        PsiFile inputPsiFile = inputData.getPsiFile();
        if (inputPsiFile instanceof FileBase) {
          FileBase psiFile = (FileBase) inputPsiFile;
          Map<String, FileModuleData> map = new HashMap<>();

          String namespace = "";
          Optional<VirtualFile> bsconfigFile =
              BsPlatform.findBsConfigForFile(inputData.getProject(), inputData.getFile());
          if (bsconfigFile.isPresent()) {
            VirtualFile parent = bsconfigFile.get().getParent();
            boolean useExternalAsSource = "bs-platform".equals(parent.getName());
            BsConfig bsConfig = BsConfigReader.read(bsconfigFile.get(), useExternalAsSource);
            if (!bsConfig.isInSources(inputData.getFile())) {
              if (LOG.isDebugEnabled()) {
                LOG.debug("»» SKIP " + inputData.getFile() + " / bsconf: " + bsconfigFile);
              }
              return Collections.emptyMap();
            }

            namespace = bsConfig.getNamespace();
          }
          String moduleName = psiFile.getModuleName();

          FileModuleData value =
              new FileModuleData(
                  inputData.getFile(),
                  namespace,
                  moduleName,
                  FileHelper.isOCaml(inputData.getFileType()),
                  psiFile.isInterface(),
                  psiFile.isComponent());
          if (LOG.isDebugEnabled()) {
            String path = inputData.getFile().getPath();
            LOG.debug(
                "indexing "
                    + Platform.removeProjectDir(inputData.getProject(), path)
                    + ": "
                    + value);
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

  @Override
  public boolean keyIsUniqueForIndexedFile() {
    return true;
  }
}
