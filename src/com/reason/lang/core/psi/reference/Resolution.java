package com.reason.lang.core.psi.reference;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

class Resolution implements Comparable<Resolution> {
    final @NotNull List<PsiQualifiedPathElement> myElements = new ArrayList<>();
    private final @Nullable String[] myPath;
    private @Nullable String[] myAlternatePath;

    int myLevel; // reverse order
    boolean myIsComplete = false;
    @Nullable Integer[] myWeights;

    public Resolution(@Nullable String[] path, @NotNull PsiQualifiedPathElement element) {
        myPath = path;
        myLevel = path == null ? -1 : myPath.length - 1;
        myElements.add(element);
    }

    public Resolution(@Nullable String[] path, @NotNull List<PsiQualifiedPathElement> elements) {
        myPath = path;
        myLevel = path == null ? -1 : myPath.length - 1;
        myElements.addAll(elements);
    }

    public static @NotNull Resolution createAlternate(@NotNull Resolution resolution, String @NotNull [] alternatePath) {
        Resolution result = new Resolution(resolution.myPath, resolution.myElements);
        int newPathLength = alternatePath.length + resolution.myPath.length;
        result.myAlternatePath = alternatePath;
        result.myLevel = newPathLength - 2;
        return result;
    }

    public @Nullable String getCurrentName() {
        if (0 <= myLevel) {
            boolean hasAlternate = myAlternatePath != null;
            int alternateLength = hasAlternate ? myAlternatePath.length : 0;
            boolean useAlternate = myLevel < alternateLength;
            return useAlternate ? myAlternatePath[myLevel] : myPath[myLevel + (hasAlternate ? 1 - alternateLength : 0)];
        }
        return null;
    }

    public @Nullable Integer getCurrentWeight() {
        return myLevel >= 0 ? myWeights[myLevel] : null;
    }

    public @Nullable Integer getWeight(int level) {
        return myWeights != null && 0 <= level && level < myPath.length ? myWeights[level] : null;
    }

    public int getFirstWeight() {
        Integer weight = myWeights == null ? null : myWeights[myWeights.length - 1];
        return weight == null ? Integer.MAX_VALUE : weight;
    }

    public void updateCurrentWeight(int weight) {
        if (myPath == null) {
            return;
        }

        if (myWeights == null) {
            int alternateLength = myAlternatePath != null ? myAlternatePath.length : 0;
            int totalLength = myAlternatePath != null ? alternateLength + myPath.length - 1 : myPath.length;
            myWeights = new Integer[totalLength];
            myLevel = totalLength - 1;
        }

        if (0 <= myLevel) {
            myWeights[myLevel] = weight;
        }

        myLevel--;
        if (myLevel < 0) {
            myIsComplete = true;
        }
    }

    public boolean isLastLevel() {
        return myLevel == 0;
    }

    public boolean isInterface() {
        PsiFile file = myElements.get(0).getContainingFile();
        return file instanceof FileBase && ((FileBase) file).isInterface();
    }

    public String getTopModuleName() {
        return myAlternatePath == null ? myPath[0] : myAlternatePath[0];
    }

    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        if (myPath != null) {
            for (int i = 0; i < myPath.length; i++) {
                String item = myPath[i];
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(item).append("-").append(getWeight(i));
            }
        }

        PsiQualifiedNamedElement element = myElements.get(0);
        return "(" + myIsComplete + ", level:" + myLevel + ", path:[" + sb.toString() + "], element: '" + element.getQualifiedName() + "' " + element.getClass().getSimpleName() + ")";
    }

    /*
    public void setAlternatePath(@NotNull String[] alternatePath) {
        int newPathLength = alternatePath.length + myPath.length;
        myAlternatePath = alternatePath;
        myLevel = newPathLength - 2;
    }
     */

    public boolean isComplete() {
        return myIsComplete;
    }

    public @NotNull String joinPath() {
        if (myAlternatePath == null) {
            return Joiner.join(".", myPath);
        }

        return Joiner.join(".", myAlternatePath) + "." + Joiner.joinFrom(".", myPath, 1);
    }

    @Override public int compareTo(@NotNull Resolution o) {
        // path with weight are before empty paths
        if (myPath == null && o.myPath == null) {
            return 0;
        }
        if (myPath == null) {
            return o.getFirstWeight() != Integer.MAX_VALUE ? 1 : -1;
        }
        if (o.myPath == null) {
            return getFirstWeight() != Integer.MAX_VALUE ? -1 : 1;
        }

        // let has more priority than record field
        if (myElements.size() == 1 && myElements.size() == o.myElements.size()) {
            PsiQualifiedPathElement myElement = myElements.get(0);
            PsiQualifiedPathElement otherElement = o.myElements.get(0);
            if (otherElement instanceof PsiRecordField && myElement instanceof PsiLet) {
                return -1;
            }
        }

        // first level with a different weight
        int r1Length = myPath.length;
        int r1Level = r1Length - 1;
        int oLength = o.myPath.length;
        int oLevel = oLength - 1;
        Integer r1Weight = getWeight(r1Level);
        Integer oWeight = o.getWeight(oLevel);
        while (0 <= r1Level && 0 <= oLevel && r1Weight != null && r1Weight.equals(oWeight)) {
            r1Level--;
            oLevel--;
            r1Weight = getWeight(r1Level);
            oWeight = o.getWeight(oLevel);
        }

        // Reach end of path for one of the element, the longest path win
        if (r1Length < oLength) {
            return 1;
        }
        if (r1Length > oLength) {
            return -1;
        }

        int levels = Integer.compare(r1Weight == null ? Integer.MAX_VALUE : r1Weight, oWeight == null ? Integer.MAX_VALUE : oWeight);
        if (levels == 0) {
            if (isInterface() && !o.isInterface()) {
                return 1;
            }
            if (!isInterface() && o.isInterface()) {
                return -1;
            }
        }

        return levels;
    }

    public boolean isPathEqualTo(@Nullable String[] otherPath) {
        if (myAlternatePath == null) {
            if (myPath == null) {
                return otherPath == null;
            }
            return otherPath != null && Arrays.equals(myPath, otherPath, String::compareTo);
        }

        if (otherPath == null) {
            return false;
        }

        int totalLength = myAlternatePath.length + myPath.length - 1;
        if (totalLength != otherPath.length) {
            return false;
        }

        for (int i = 0; i < totalLength; i++) {
            String value = i < myAlternatePath.length ? myAlternatePath[i] : myPath[i - 1];
            if (!value.equals(otherPath[i])) {
                return false;
            }
        }

        return true;
    }

    public @NotNull String[] augmentPath(@NotNull String[] path) {
        if (myAlternatePath == null && myPath.length == 1) {
            return path;
        }

        int totalLength = (myAlternatePath == null ? 1 : myAlternatePath.length) + myPath.length - 1;
        String[] newPath = new String[path.length + totalLength - 1];

        System.arraycopy(path, 0, newPath, 0, path.length);
        if (myAlternatePath == null) {
            System.arraycopy(myPath, 1, newPath, path.length, myPath.length - 1);
        } else {
            System.arraycopy(myAlternatePath, 1, newPath, path.length, myAlternatePath.length - 1);
            System.arraycopy(myPath, 1, newPath, path.length + myAlternatePath.length - 1, myPath.length - 1);
        }

        return newPath;
    }
}
