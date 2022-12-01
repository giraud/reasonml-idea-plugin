package com.reason.ide.structure;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import org.junit.*;

public class DuneProjectStructureTest extends ORBasePlatformTestCase {
    @Test
    public void test_stanza() {
        DuneFile a = (DuneFile) myFixture.configureByText("dune-project", "(lang dune 2.9) (licence MIT)");
        StructureViewModel model = new ORStructureViewModel(a);

        TreeElement[] children = model.getRoot().getChildren();
        assertEquals("lang", children[0].getPresentation().getPresentableText());
        assertEquals("licence", children[1].getPresentation().getPresentableText());
        assertSize(2, children);
    }

    @Test
    public void test_fields() {
        DuneFile a = (DuneFile) myFixture.configureByText("dune-project", "(package (name xxx) (github aaa/bbb))");
        StructureViewModel model = new ORStructureViewModel(a);

        TreeElement pkg = model.getRoot().getChildren()[0];
        TreeElement[] children = pkg.getChildren();
        assertEquals("name", children[0].getPresentation().getPresentableText());
        assertEquals("github", children[1].getPresentation().getPresentableText());
        assertSize(2, children);
    }
}
