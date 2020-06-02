package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.DuneFile;
import com.reason.ide.files.MlyFile;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.RmlFile;
import org.jetbrains.annotations.NotNull;

public class StructureViewModel extends StructureViewModelBase implements com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider {
    StructureViewModel(@NotNull PsiFile psiFile) {
        super(psiFile, new StructureViewElement(psiFile));
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof RmlFile || element instanceof OclFile || element instanceof MlyFile || element instanceof DuneFile;
    }
}
