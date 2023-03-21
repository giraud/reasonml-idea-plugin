package com.reason.ide.search.reference;

import com.intellij.openapi.util.io.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.gist.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

/**
 * Generate a cache for a PsiFile that contains functor QName (resolved) for every functor call present in the file.
 */
public class ORFunctorPsiGist {
    private static final int VERSION = 1;
    private static final String ID = "functor-gist";
    private static final PsiFileGist<Map<String, String[]>> myGist = GistManager.getInstance().newPsiFileGist(ID, VERSION, new ORFunctorPsiGist.Externalizer(), ORFunctorPsiGist::getFileData);

    private ORFunctorPsiGist() {
    }

    public static Map<String, String[]> getData(@Nullable PsiFile file) {
        return file == null ? null : myGist.getFileData(file);
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

        Collection<RPsiFunctorCall> functorCalls = PsiTreeUtil.findChildrenOfType(file, RPsiFunctorCall.class);
        for (RPsiFunctorCall functorCall : functorCalls) {
            RPsiFunctor resolvedFunctor = functorCall.resolveFunctor();
            if (resolvedFunctor != null) {
                result.put(((RPsiInnerModuleImpl) functorCall.getParent().getParent()).getQualifiedName(), resolvedFunctor.getQualifiedNameAsPath());
            }
        }

        return result;
    }

}
