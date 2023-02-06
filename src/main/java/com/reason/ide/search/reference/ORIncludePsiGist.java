package com.reason.ide.search.reference;

import com.intellij.openapi.util.io.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.gist.*;
import com.intellij.util.io.*;
import com.reason.ide.search.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public class ORIncludePsiGist {
    private ORIncludePsiGist() {
    }

    private static final int VERSION = 1;
    private static final String ID = "include";
    private static final PsiFileGist<Map<String, String[]>> myGist = GistManager.getInstance().newPsiFileGist(ID, VERSION, new ORIncludePsiGist.Externalizer(), ORIncludePsiGist::getFileData);

    public static @Nullable Map<String, String[]> getData(@Nullable PsiFile psiFile) {
        return psiFile == null ? null : myGist.getFileData(psiFile);
    }

    public static class Externalizer implements DataExternalizer<Map<String, String[]>> {
        @Override
        public void save(@NotNull DataOutput out, Map<String, String[]> value) throws IOException {
            DataInputOutputUtilRt.writeMap(out, value, out::writeUTF, strings -> {
                List<String> paths = Arrays.asList(strings);
                DataInputOutputUtilRt.writeSeq(out, paths, out::writeUTF);
            });
        }

        @Override
        public Map<String, String[]> read(@NotNull DataInput in) throws IOException {
            return DataInputOutputUtilRt.readMap(in, in::readUTF, () -> {
                List<String> paths = DataInputOutputUtilRt.readSeq(in, in::readUTF);
                return paths.toArray(new String[0]);
            });
        }
    }

    public static Map<String, String[]> getFileData(@NotNull PsiFile file) {
        Map<String, String[]> result = new HashMap<>();

        Collection<RPsiInclude> includes = PsiTreeUtil.findChildrenOfType(file, RPsiInclude.class);
        for (RPsiInclude include : includes) {
            String[] resolvedPath = getResolvedPath(include);
            if (resolvedPath != null) {
                result.put(Joiner.join(".", include.getQualifiedPath()) + "." + include.getIncludePath(), resolvedPath);
            }
        }

        return result;
    }

    public static String @Nullable [] getResolvedPath(@NotNull RPsiInclude include) {   // in PsiFileGist
        String includePath = include.getIncludePath();
        PsiFinder psiFinder = include.getProject().getService(PsiFinder.class);
        RPsiQualifiedPathElement resolvedElement = psiFinder.findModuleBack(include, includePath);

        String path = resolvedElement == null ? includePath : resolvedElement.getQualifiedName();
        return path == null ? null : path.split("\\.");
    }

}
