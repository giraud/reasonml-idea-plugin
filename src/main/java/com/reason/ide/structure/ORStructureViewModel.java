package com.reason.ide.structure;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

public class ORStructureViewModel extends StructureViewModelBase implements com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider {
    ORStructureViewModel(@NotNull PsiFile psiFile) {
        super(psiFile, new StructureViewElement(psiFile, 1));
    }

    public Sorter @NotNull [] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }

    @Override
    public Filter @NotNull [] getFilters() {
        return new Filter[]{new NestedFunctionsFilter()};
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof RmlFile
                || element instanceof ResFile
                || element instanceof OclFile
                || element instanceof MlyFile
                || element instanceof DuneFile;
    }
}
