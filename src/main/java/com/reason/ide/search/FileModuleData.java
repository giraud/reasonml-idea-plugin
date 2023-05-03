package com.reason.ide.search;

import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class FileModuleData implements Comparable<FileModuleData>, IndexedFileModule {
    private final String myNamespace;
    private final String myModuleName;
    private final String myFullName;
    private final String myPath;
    private final boolean myIsOCaml;
    private final boolean myIsRescript;
    private final boolean myIsInterface;
    private final boolean myIsComponent;
    private final boolean myHasComponents;

    public FileModuleData(@NotNull VirtualFile file, @NotNull String namespace, String moduleName, boolean isOCaml, boolean isRescript, boolean isInterface, boolean isComponent, boolean hasComponents) {
        myNamespace = namespace;
        myModuleName = moduleName;
        myIsOCaml = isOCaml;
        myIsRescript = isRescript;
        myIsInterface = isInterface;
        myIsComponent = isComponent;
        myHasComponents = hasComponents;

        myPath = file.getPath();
        String filename = file.getNameWithoutExtension();
        myFullName = namespace.isEmpty() ? filename : filename + "-" + namespace;
    }

    public FileModuleData(String path, String fullName, String namespace, String moduleName, boolean isOCaml, boolean isRescript, boolean isInterface, boolean isComponent, boolean hasComponents) {
        myPath = path;
        myFullName = fullName;
        myNamespace = namespace;
        myModuleName = moduleName;
        myIsOCaml = isOCaml;
        myIsRescript = isRescript;
        myIsInterface = isInterface;
        myIsComponent = isComponent;
        myHasComponents = hasComponents;
    }

    @Override
    public @NotNull String getNamespace() {
        return myNamespace;
    }

    public boolean hasNamespace() {
        return !myNamespace.isEmpty();
    }

    @Override
    public @NotNull String getModuleName() {
        return myModuleName;
    }

    @NotNull
    @Override
    public String getPath() {
        return myPath;
    }

    @NotNull
    @Override
    public String getFullName() {
        return myFullName;
    }

    @Override
    public boolean isOCaml() {
        return myIsOCaml;
    }

    @Override
    public boolean isRescript() {
        return myIsRescript;
    }

    @Override
    public boolean isInterface() {
        return myIsInterface;
    }

    @Override
    public boolean isComponent() {
        return myIsComponent;
    }

    public boolean hasComponents() {
        return myHasComponents;
    }

    @Override
    public int compareTo(@NotNull FileModuleData o) {
        int comp = Comparing.compare(myNamespace, o.myNamespace);
        if (comp == 0) {
            comp = Comparing.compare(myModuleName, o.myModuleName);
            if (comp == 0) {
                comp = Comparing.compare(myIsInterface, o.myIsInterface);
            }
        }
        return comp;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileModuleData that = (FileModuleData) o;
        return myIsInterface == that.myIsInterface
                && Objects.equals(myNamespace, that.myNamespace)
                && Objects.equals(myModuleName, that.myModuleName)
                && Objects.equals(myPath, that.myPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myNamespace, myModuleName, myIsInterface, myPath);
    }

    @NotNull
    @Override
    public String toString() {
        return "FileModuleData{"
                + "namespace='" + myNamespace + '\''
                + ", moduleName='" + myModuleName + '\''
                + ", isInterface=" + myIsInterface
                + ", isComponent=" + myIsComponent
                + ", hasComponents=" + myHasComponents
                + ", path=" + myPath
                + '}';
    }
}
