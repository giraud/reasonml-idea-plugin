package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import com.reason.psi.ReasonMLFile;
import org.jetbrains.annotations.NotNull;

public class ReasonMLStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {
    public  ReasonMLStructureViewModel(PsiFile psiFile) {
        super(psiFile, new ReasonMLStructureViewElement(psiFile));
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
        return element instanceof ReasonMLFile;
    }
}
